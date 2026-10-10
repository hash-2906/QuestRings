package com.questrings.domain.repository

import com.questrings.domain.model.medal.MedalTier
import kotlinx.coroutines.flow.Flow

interface MedalRepository {
    fun observeCurrentMedal(userId: String): Flow<MedalTier>
    suspend fun updateMedal(userId: String, tier: MedalTier): Result<Unit>
    suspend fun getLastUnlockedMedal(userId: String): Result<MedalTier>
}
