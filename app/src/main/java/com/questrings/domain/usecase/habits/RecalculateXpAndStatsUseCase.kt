package com.questrings.domain.usecase.habits

import com.questrings.domain.model.CompletionResult
import com.questrings.domain.model.Habit
import com.questrings.domain.model.HabitCategory
import com.questrings.domain.model.UserStats
import com.questrings.domain.repository.CompletionRepository
import com.questrings.domain.repository.HabitRepository
import com.questrings.domain.repository.StatsRepository
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.math.floor

class RecalculateXpAndStatsUseCase @Inject constructor(
    private val statsRepository: StatsRepository,
    private val habitRepository: HabitRepository,
    private val completionRepository: CompletionRepository
) {
    suspend operator fun invoke(
        userId: String,
        targetHabit: Habit,
        isCompleting: Boolean,
        dateKey: String = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    ): CompletionResult {
        val currentStats = statsRepository.observeUserStats(userId).firstOrNull() ?: UserStats(userId = userId)
        val habits = habitRepository.observeHabitsForUser(userId).firstOrNull() ?: emptyList()
        val completions = completionRepository.observeTodayCompletions(userId, dateKey).firstOrNull() ?: emptyList()
        val completedHabitIds = completions.map { it.habitId }.toSet()

        // 1. Calculate streak
        val yesterdayKey = getYesterdayKey()
        val streak = if (isCompleting) {
            when {
                currentStats.lastActivityDate == dateKey -> currentStats.currentStreak.coerceAtLeast(1)
                currentStats.lastActivityDate == yesterdayKey -> currentStats.currentStreak + 1
                currentStats.lastActivityDate.isBlank() -> 1
                else -> 1 // Gap > 1 day
            }
        } else {
            currentStats.currentStreak
        }

        val longestStreak = maxOf(currentStats.longestStreak, streak)

        // 2. Streak Multiplier
        val multiplier = getStreakMultiplier(streak)

        // 3. Ring Closure Checks
        val categoryHabits = habits.filter { it.category == targetHabit.category }
        val categoryAllDone = categoryHabits.isNotEmpty() && categoryHabits.all { completedHabitIds.contains(it.id) }
        val closedCategory = if (isCompleting && categoryAllDone) targetHabit.category else null

        // All 3 rings check
        val allRingsDone = HabitCategory.entries.all { cat ->
            val catList = habits.filter { it.category == cat }
            catList.isNotEmpty() && catList.all { completedHabitIds.contains(it.id) }
        }

        // 4. Base XP Calculation
        var baseXp = if (isCompleting) 25 else -25
        if (isCompleting) {
            if (closedCategory != null) baseXp += 100
            if (allRingsDone) baseXp += 200
        }

        val finalXp = if (isCompleting) {
            floor(baseXp * multiplier).toInt()
        } else {
            baseXp
        }

        // 5. Update Total XP & Level
        val newTotalXp = (currentStats.xp + finalXp).coerceAtLeast(0)
        var level = currentStats.level
        var xpRequiredForNext = level * 500
        var didLevelUp = false

        while (newTotalXp >= xpRequiredForNext) {
            level++
            xpRequiredForNext += level * 500
            didLevelUp = true
        }

        // 6. Category XP
        val currentCategoryXp = currentStats.xpByCategory[targetHabit.category] ?: 0
        val updatedCategoryXp = (currentCategoryXp + finalXp).coerceAtLeast(0)
        val updatedXpByCategory = currentStats.xpByCategory.toMutableMap().apply {
            put(targetHabit.category, updatedCategoryXp)
        }

        val totalHabitsDone = if (isCompleting) currentStats.totalHabitsCompleted + 1 else (currentStats.totalHabitsCompleted - 1).coerceAtLeast(0)

        val updatedStats = currentStats.copy(
            xp = newTotalXp,
            level = level,
            currentStreak = streak,
            longestStreak = longestStreak,
            lastActivityDate = if (isCompleting) dateKey else currentStats.lastActivityDate,
            totalHabitsCompleted = totalHabitsDone,
            xpByCategory = updatedXpByCategory
        )

        statsRepository.updateStats(updatedStats)

        return CompletionResult(
            xpGained = finalXp,
            newTotalXp = newTotalXp,
            didLevelUp = didLevelUp,
            newLevel = level,
            closedCategory = closedCategory,
            allRingsClosed = isCompleting && allRingsDone,
            newStreak = streak,
            multiplierApplied = multiplier
        )
    }

    private fun getStreakMultiplier(streak: Int): Float {
        return when {
            streak >= 30 -> 2.0f
            streak >= 14 -> 1.75f
            streak >= 7 -> 1.5f
            streak >= 3 -> 1.25f
            else -> 1.0f
        }
    }

    private fun getYesterdayKey(): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -1)
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cal.time)
    }
}
