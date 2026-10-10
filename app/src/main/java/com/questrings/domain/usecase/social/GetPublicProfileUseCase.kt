package com.questrings.domain.usecase.social

import com.questrings.domain.model.social.PublicProfile
import com.questrings.domain.repository.ProfileRepository
import javax.inject.Inject

class GetPublicProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(
        targetUserId: String,
        currentUserId: String
    ): Result<PublicProfile> = profileRepository.getPublicProfile(targetUserId, currentUserId)
}
