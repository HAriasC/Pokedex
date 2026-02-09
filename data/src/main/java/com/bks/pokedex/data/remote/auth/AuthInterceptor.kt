package com.bks.pokedex.data.remote.auth

import com.bks.pokedex.domain.repository.AuthRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * Interceptor encargado de inyectar automáticamente el token de autenticación en las peticiones de red.
 *
 * Implementa una lógica proactiva que verifica la expiración del token antes de realizar la petición,
 * optimizando el tráfico de red.
 */
class AuthInterceptor @Inject constructor(
    private val authRepository: AuthRepository
) : Interceptor {

    companion object {
        const val HEADER_NO_AUTHENTICATION = "No-Authentication"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        if (originalRequest.header("Authorization") != null ||
            originalRequest.header(HEADER_NO_AUTHENTICATION) != null
        ) {
            val newRequest = originalRequest.newBuilder()
                .removeHeader(HEADER_NO_AUTHENTICATION)
                .build()
            return chain.proceed(newRequest)
        }

        // Lógica Proactiva de OAuth 2.0:
        // Verificamos si el token ya expiró localmente. Si es así, forzamos un refresco antes
        // de que la petición salga a la red, ahorrando una respuesta 401 innecesaria.
        if (authRepository.isTokenExpired()) {
            runBlocking {
                authRepository.refreshToken()
            }
        }

        val token = runBlocking {
            authRepository.getAccessToken().first()
        }

        val requestBuilder = originalRequest.newBuilder()
        token?.let {
            requestBuilder.header("Authorization", "Bearer $it")
        }

        return chain.proceed(requestBuilder.build())
    }
}
