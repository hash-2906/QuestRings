package com.questrings.domain.repository

import com.questrings.domain.model.social.PublicProfile

interface ProfileRepository {
    suspend fun getPublicProfile(targetUserId: String, currentUserId: String): Result<PublicProfile>
}
