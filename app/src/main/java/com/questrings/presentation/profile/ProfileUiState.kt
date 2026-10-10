package com.questrings.presentation.profile

import com.questrings.domain.config.MedalThreshold
import com.questrings.domain.model.User
import com.questrings.domain.model.UserStats
import com.questrings.domain.model.medal.MedalTier

data class ProfileUiState(
    val isLoading: Boolean = true,
    val user: User? = null,
    val stats: UserStats? = null,
    val currentMedal: MedalTier = MedalTier.NONE,
    val nextMedalThreshold: MedalThreshold? = null,
    val progressToNextMedal: Float = 0f,
    val currentLevelXp: Int = 0,
    val requiredLevelXp: Int = 500,
    val levelProgress: Float = 0f,
    val totalXp: Int = 0,
    val streak: Int = 0,
    val isDeletingAccount: Boolean = false,
    val deleteAccountError: String? = null,
    val isAccountDeleted: Boolean = false
)
