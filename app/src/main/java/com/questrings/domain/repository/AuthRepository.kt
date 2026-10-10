package com.questrings.domain.repository

import com.questrings.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<User?>
    suspend fun registerWithEmail(email: String, password: String): Result<User>
    suspend fun loginWithEmail(email: String, password: String): Result<User>
    suspend fun signInWithGoogle(idToken: String): Result<User>
    suspend fun saveUserProfile(user: User): Result<Unit>
    suspend fun getUserProfile(uid: String): Result<User>
    suspend fun signOut()
    suspend fun logout() = signOut()
    suspend fun deleteAccount(password: String? = null): Result<Unit>
}
