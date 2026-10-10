package com.questrings.domain.usecase.auth

import com.questrings.domain.model.User
import com.questrings.domain.repository.AuthRepository
import javax.inject.Inject

class LoginWithEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        return authRepository.loginWithEmail(email, password)
    }
}
