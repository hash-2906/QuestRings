package com.questrings.domain.usecase.social

import com.questrings.domain.model.social.PublicProfile
import com.questrings.domain.repository.SocialRepository
import javax.inject.Inject

class SearchUsersUseCase @Inject constructor(
    private val socialRepository: SocialRepository
) {
    suspend operator fun invoke(
        query: String,
        currentUserId: String
    ): Result<List<PublicProfile>> = socialRepository.searchUsers(query, currentUserId)
}
