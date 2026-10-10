package com.questrings.domain.usecase.auth

import com.questrings.domain.model.User
import com.questrings.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterWithEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        return authRepository.registerWithEmail(email, password)
    }
}
