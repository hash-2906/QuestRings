package com.questrings.domain.usecase.auth

import com.questrings.domain.model.User
import com.questrings.domain.repository.AuthRepository
import javax.inject.Inject

class SaveUserProfileUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(user: User): Result<Unit> {
        return authRepository.saveUserProfile(user)
    }
}
