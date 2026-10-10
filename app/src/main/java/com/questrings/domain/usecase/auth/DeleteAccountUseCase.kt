package com.questrings.domain.usecase.auth

import com.questrings.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Executes mandatory Google Play account deletion:
 * re-authenticates, wipes Room DB, deletes Firestore records, and removes Firebase Auth credentials.
 */
class DeleteAccountUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(password: String? = null): Result<Unit> {
        return authRepository.deleteAccount(password)
    }
}
