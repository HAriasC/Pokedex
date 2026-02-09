package com.bks.pokedex.data.repository

import com.bks.pokedex.data.local.prefs.AuthLocalDataSource
import com.bks.pokedex.domain.repository.AuthRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton
import java.util.UUID

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val localDataSource: AuthLocalDataSource
) : AuthRepository {

    private val refreshTokenMutex = Mutex()

    private val _isLoggedInFlow = MutableStateFlow(localDataSource.isLoggedIn())
    private val _accessTokenFlow = MutableStateFlow(localDataSource.getAccessToken())

    override fun isLoggedIn(): Flow<Boolean> = _isLoggedInFlow.asStateFlow()

    override fun getAccessToken(): Flow<String?> = _accessTokenFlow.asStateFlow()

    override fun isTokenExpired(): Boolean {
        val expirationTime = localDataSource.getTokenExpiration()
        return System.currentTimeMillis() >= expirationTime
    }

    override suspend fun login(user: String, password: String): Result<Unit> {
        delay(2000)

        return when {
            user.lowercase() == "error@pokedex.com" -> {
                Result.failure(Exception("Error de autenticación: El usuario ha sido bloqueado o no existe."))
            }

            user.isNotBlank() && password.length >= 4 -> {
                val accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.${UUID.randomUUID()}"
                val refreshToken = UUID.randomUUID().toString()
                // Simulamos que el token expira en 1 hora (3600 segundos)
                val expiresIn = 3600L

                localDataSource.saveSession(user, accessToken, refreshToken, expiresIn)

                _accessTokenFlow.value = accessToken
                _isLoggedInFlow.value = true
                Result.success(Unit)
            }

            else -> {
                Result.failure(Exception("Credenciales inválidas. Verifica tu usuario y contraseña."))
            }
        }
    }

    override suspend fun logout() {
        localDataSource.clearSession()
        _accessTokenFlow.value = null
        _isLoggedInFlow.value = false
    }

    override suspend fun refreshToken(): Result<String> = refreshTokenMutex.withLock {
        delay(1000)

        val refreshToken = localDataSource.getRefreshToken()

        return if (refreshToken != null) {
            val newAccessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.${UUID.randomUUID()}"
            // Al refrescar, renovamos por otra hora
            val expiresIn = 3600L

            localDataSource.updateAccessToken(newAccessToken, expiresIn)
            _accessTokenFlow.value = newAccessToken
            Result.success(newAccessToken)
        } else {
            Result.failure(Exception("No refresh token available"))
        }
    }
}
