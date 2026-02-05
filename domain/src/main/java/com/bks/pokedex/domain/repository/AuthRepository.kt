package com.bks.pokedex.domain.repository

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun isLoggedIn(): Flow<Boolean>
    suspend fun login(user: String, password: String): Result<Unit>
    suspend fun logout()
}
