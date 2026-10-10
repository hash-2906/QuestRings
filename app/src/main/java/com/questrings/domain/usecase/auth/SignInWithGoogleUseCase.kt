package com.questrings.domain.usecase.auth

import com.questrings.domain.model.User
import com.questrings.domain.repository.AuthRepository
import javax.inject.Inject

class SignInWithGoogleUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(idToken: String): Result<User> {
        return authRepository.signInWithGoogle(idToken)
    }
}
