package com.bks.pokedex.domain.repository

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun isLoggedIn(): Flow<Boolean>
    fun getAccessToken(): Flow<String?>
    fun isTokenExpired(): Boolean
    suspend fun login(user: String, password: String): Result<Unit>
    suspend fun logout()
    suspend fun refreshToken(): Result<String>
}
