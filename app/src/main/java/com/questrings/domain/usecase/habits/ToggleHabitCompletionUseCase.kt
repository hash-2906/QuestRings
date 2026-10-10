package com.questrings.domain.usecase.habits

import com.questrings.domain.model.CompletionResult
import com.questrings.domain.model.Habit
import com.questrings.domain.model.HabitCompletion
import com.questrings.domain.repository.CompletionRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

class ToggleHabitCompletionUseCase @Inject constructor(
    private val completionRepository: CompletionRepository,
    private val recalculateXpAndStatsUseCase: RecalculateXpAndStatsUseCase
) {
    suspend operator fun invoke(
        habit: Habit,
        isCurrentlyDone: Boolean,
        userId: String,
        dateKey: String = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    ): Result<CompletionResult> {
        return try {
            if (!isCurrentlyDone) {
                val completion = HabitCompletion(
                    id = UUID.randomUUID().toString(),
                    habitId = habit.id,
                    userId = userId,
                    completedAt = System.currentTimeMillis(),
                    dateKey = dateKey
                )
                completionRepository.markComplete(completion)
                val result = recalculateXpAndStatsUseCase(userId, habit, isCompleting = true, dateKey)
                Result.success(result)
            } else {
                completionRepository.unmarkComplete(habit.id, dateKey)
                val result = recalculateXpAndStatsUseCase(userId, habit, isCompleting = false, dateKey)
                Result.success(result)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
