package com.questrings.data.repository

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.questrings.domain.config.MedalProgressionConfig
import com.questrings.domain.model.medal.MedalTier
import com.questrings.domain.repository.MedalRepository
import com.questrings.domain.repository.StatsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MedalRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val statsRepository: StatsRepository,
    private val firestore: FirebaseFirestore
) : MedalRepository {

    private val prefs = context.getSharedPreferences("questrings_medals", Context.MODE_PRIVATE)

    override fun observeCurrentMedal(userId: String): Flow<MedalTier> {
        return statsRepository.observeUserStats(userId).map { stats ->
            val stored = prefs.getString("medal_$userId", null)
            val evaluated = MedalProgressionConfig.evaluate(stats.xp, stats.level)
            if (stored != null) {
                val storedTier = runCatching { MedalTier.valueOf(stored) }.getOrDefault(MedalTier.NONE)
                if (storedTier.ordinal >= evaluated.ordinal) storedTier else evaluated
            } else {
                evaluated
            }
        }
    }

    override suspend fun updateMedal(userId: String, tier: MedalTier): Result<Unit> = runCatching {
        prefs.edit().putString("medal_$userId", tier.name).apply()
        try {
            firestore.collection("users")
                .document(userId)
                .set(mapOf("currentMedal" to tier.name), SetOptions.merge())
                .await()
        } catch (_: Exception) {
            // Best effort offline
        }
    }

    override suspend fun getLastUnlockedMedal(userId: String): Result<MedalTier> = runCatching {
        val stored = prefs.getString("medal_$userId", null)
        if (stored != null) {
            runCatching { MedalTier.valueOf(stored) }.getOrDefault(MedalTier.NONE)
        } else {
            val stats = statsRepository.observeUserStats(userId).firstOrNull()
            if (stats != null) {
                MedalProgressionConfig.evaluate(stats.xp, stats.level)
            } else {
                MedalTier.NONE
            }
        }
    }
}
