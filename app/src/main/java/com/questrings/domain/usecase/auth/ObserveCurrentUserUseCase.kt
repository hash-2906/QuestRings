package com.questrings.domain.usecase.auth

import com.questrings.domain.model.User
import com.questrings.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveCurrentUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<User?> {
        return authRepository.currentUser
    }
}
