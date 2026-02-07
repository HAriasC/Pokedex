package com.bks.pokedex.domain.usecase

import com.bks.pokedex.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(user: String, pass: String): Result<Unit> {
        return repository.login(user = user, password = pass)
    }
}
