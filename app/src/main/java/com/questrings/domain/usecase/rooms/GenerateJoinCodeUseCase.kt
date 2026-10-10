package com.questrings.domain.usecase.rooms

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class GenerateJoinCodeUseCase @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    // Excluding confusing characters: O, 0, I, 1
    private val charset = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"

    suspend operator fun invoke(): String {
        for (attempt in 1..5) {
            val code = (1..6).map { charset.random() }.joinToString("")
            val doc = firestore.collection("roomCodes").document(code).get().await()
            if (!doc.exists()) {
                return code
            }
        }
        // Expand to 8 chars on extreme collision
        return (1..8).map { charset.random() }.joinToString("")
    }
}
