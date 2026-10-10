package com.questrings.data.mapper

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.questrings.domain.model.User

object UserMapper {
    fun fromFirestore(doc: DocumentSnapshot): User? {
        if (!doc.exists()) return null
        return User(
            uid = doc.getString("uid") ?: doc.id,
            email = doc.getString("email") ?: "",
            displayName = doc.getString("displayName") ?: "",
            avatarColorHex = doc.getString("avatarColorHex") ?: "#7E5DFF",
            xp = doc.getLong("xp")?.toInt() ?: 0,
            level = doc.getLong("level")?.toInt() ?: 1,
            streak = doc.getLong("streak")?.toInt() ?: 0,
            createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()
        )
    }

    fun toFirestore(user: User): Map<String, Any> {
        return mapOf(
            "uid" to user.uid,
            "email" to user.email,
            "displayName" to user.displayName,
            "avatarColorHex" to user.avatarColorHex,
            "xp" to user.xp,
            "level" to user.level,
            "streak" to user.streak,
            "createdAt" to user.createdAt
        )
    }

    fun fromFirebaseUser(firebaseUser: FirebaseUser): User {
        return User(
            uid = firebaseUser.uid,
            email = firebaseUser.email ?: "",
            displayName = firebaseUser.displayName ?: "",
            avatarColorHex = "#7E5DFF",
            xp = 0,
            level = 1,
            streak = 0,
            createdAt = System.currentTimeMillis()
        )
    }
}
