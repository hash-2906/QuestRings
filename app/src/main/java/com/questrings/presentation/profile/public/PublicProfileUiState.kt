package com.questrings.presentation.profile.public

import com.questrings.domain.model.social.PublicProfile

data class PublicProfileUiState(
    val profile: PublicProfile? = null,
    val isLoading: Boolean = true,
    val isActionInProgress: Boolean = false
)
