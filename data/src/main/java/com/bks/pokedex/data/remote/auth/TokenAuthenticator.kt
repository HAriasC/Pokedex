package com.bks.pokedex.data.remote.auth

import com.bks.pokedex.domain.repository.AuthRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Automatiza el proceso de refresco del token de acceso cuando expira.
 *
 * Esta clase es invocada por OkHttp cuando se recibe una respuesta 401 Unauthorized.
 * Utiliza un [Mutex] para manejar la concurrencia, asegurando que ante múltiples peticiones
 * fallidas simultáneas, solo una intente refrescar el token mientras las demás esperan el resultado.
 */
@Singleton
class TokenAuthenticator @Inject constructor(
    private val authRepository: AuthRepository
) : Authenticator {

    // Mutex para garantizar la seguridad entre hilos durante el refresco, evitando llamadas duplicadas.
    private val mutex = Mutex()

    override fun authenticate(route: Route?, response: Response): Request? {
        // Esta función se ejecuta automáticamente cuando el servidor devuelve un 401.

        return runBlocking {
            mutex.withLock {
                val currentToken = authRepository.getAccessToken().first()
                val tokenInFailedRequest =
                    response.request.header("Authorization")?.substringAfter("Bearer ")

                // 1. Verificación de sincronización: Si el token ya fue actualizado por otro hilo 
                // mientras este esperaba el bloqueo, simplemente reintentamos con el nuevo token.
                if (tokenInFailedRequest != null && tokenInFailedRequest != currentToken) {
                    return@withLock response.request.newBuilder()
                        .header("Authorization", "Bearer $currentToken")
                        .build()
                }

                // 2. Ejecutar el refresco del token ya que es el primer intento o el token sigue siendo antiguo.
                val result = authRepository.refreshToken()

                if (result.isSuccess) {
                    val newToken = result.getOrNull()
                    // 3. Éxito: Reintenta la petición original con el nuevo token obtenido.
                    response.request.newBuilder()
                        .header("Authorization", "Bearer $newToken")
                        .build()
                } else {
                    // 4. Fallo: Si el refresh token también expiró, forzamos el cierre de sesión.
                    authRepository.logout()
                    null
                }
            }
        }
    }
}
