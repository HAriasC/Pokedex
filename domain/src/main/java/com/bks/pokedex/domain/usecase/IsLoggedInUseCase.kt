package com.bks.pokedex.domain.usecase

import com.bks.pokedex.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class IsLoggedInUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke(): Flow<Boolean> = repository.isLoggedIn()
}
