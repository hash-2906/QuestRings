package com.questrings.data.repository

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
import com.questrings.data.local.QuestRingsDatabase
import com.questrings.data.mapper.UserMapper
import com.questrings.domain.model.User
import com.questrings.domain.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val database: QuestRingsDatabase
) : AuthRepository {

    override val currentUser: Flow<User?> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            val firebaseUser = auth.currentUser
            if (firebaseUser == null) {
                trySend(null)
            } else {
                firestore.collection("users").document(firebaseUser.uid)
                    .get()
                    .addOnSuccessListener { snapshot ->
                        val user = UserMapper.fromFirestore(snapshot)
                            ?: UserMapper.fromFirebaseUser(firebaseUser)
                        trySend(user)
                    }
                    .addOnFailureListener {
                        trySend(UserMapper.fromFirebaseUser(firebaseUser))
                    }
            }
        }

        firebaseAuth.addAuthStateListener(authStateListener)
        awaitClose {
            firebaseAuth.removeAuthStateListener(authStateListener)
        }
    }

    override suspend fun registerWithEmail(email: String, password: String): Result<User> {
        return try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email.trim(), password).await()
            val firebaseUser = authResult.user
                ?: return Result.failure(Exception("Registration failed: no user returned."))

            val newUser = User(
                uid = firebaseUser.uid,
                email = firebaseUser.email ?: email.trim(),
                displayName = "",
                avatarColorHex = "#7E5DFF",
                xp = 0,
                level = 1,
                streak = 0,
                createdAt = System.currentTimeMillis()
            )

            // Save initial user doc
            saveUserProfile(newUser)

            Result.success(newUser)
        } catch (e: Exception) {
            Result.failure(mapAuthException(e))
        }
    }

    override suspend fun loginWithEmail(email: String, password: String): Result<User> {
        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email.trim(), password).await()
            val firebaseUser = authResult.user
                ?: return Result.failure(Exception("Sign in failed: no user returned."))

            val userProfileResult = getUserProfile(firebaseUser.uid)
            val user = userProfileResult.getOrElse {
                UserMapper.fromFirebaseUser(firebaseUser)
            }
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(mapAuthException(e))
        }
    }

    override suspend fun signInWithGoogle(idToken: String): Result<User> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = firebaseAuth.signInWithCredential(credential).await()
            val firebaseUser = authResult.user
                ?: return Result.failure(Exception("Google Sign-In failed: no user returned."))

            val existingProfile = getUserProfile(firebaseUser.uid)
            val user = existingProfile.getOrElse {
                val newUser = User(
                    uid = firebaseUser.uid,
                    email = firebaseUser.email ?: "",
                    displayName = firebaseUser.displayName ?: "",
                    avatarColorHex = "#7E5DFF",
                    xp = 0,
                    level = 1,
                    streak = 0,
                    createdAt = System.currentTimeMillis()
                )
                saveUserProfile(newUser)
                newUser
            }
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(mapAuthException(e))
        }
    }

    override suspend fun saveUserProfile(user: User): Result<Unit> {
        return try {
            firestore.collection("users")
                .document(user.uid)
                .set(UserMapper.toFirestore(user))
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(mapAuthException(e))
        }
    }

    override suspend fun getUserProfile(uid: String): Result<User> {
        return try {
            val doc = firestore.collection("users").document(uid).get().await()
            val user = UserMapper.fromFirestore(doc)
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("User profile not found."))
            }
        } catch (e: Exception) {
            Result.failure(mapAuthException(e))
        }
    }

    override suspend fun signOut() {
        try {
            firebaseAuth.signOut()
        } catch (_: Exception) {}
    }

    override suspend fun logout() {
        signOut()
    }

    override suspend fun deleteAccount(password: String?): Result<Unit> {
        return try {
            val user = firebaseAuth.currentUser
                ?: return Result.failure(Exception("No authenticated user session found."))
            val uid = user.uid

            // 1. Re-authenticate if password is provided
            if (!password.isNullOrBlank() && !user.email.isNullOrBlank()) {
                val credential = com.google.firebase.auth.EmailAuthProvider.getCredential(user.email!!, password)
                user.reauthenticate(credential).await()
            }

            // 2. Clear local Room database tables
            withContext(Dispatchers.IO) {
                database.clearAllTables()
            }

            // 3. Delete Firestore user document
            try {
                firestore.collection("users").document(uid).delete().await()
            } catch (_: Exception) {}

            // 4. Remove user from room memberships
            try {
                val memberships = firestore.collectionGroup("members")
                    .whereEqualTo("userId", uid)
                    .get()
                    .await()
                for (doc in memberships.documents) {
                    doc.reference.delete().await()
                }
            } catch (_: Exception) {}

            // 5. Delete Firebase Auth user
            user.delete().await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(mapAuthException(e))
        }
    }

    private fun mapAuthException(e: Exception): Exception {
        return when (e) {
            is FirebaseNetworkException -> Exception("No connection. Check your internet and try again.")
            is FirebaseAuthInvalidCredentialsException -> Exception("Incorrect email or password.")
            is FirebaseAuthUserCollisionException -> Exception("An account with this email already exists.")
            else -> Exception(e.localizedMessage ?: "An unexpected authentication error occurred.")
        }
    }
}
