package com.questrings.domain.usecase.habits

import com.questrings.domain.model.HabitWithStatus
import com.questrings.domain.repository.CompletionRepository
import com.questrings.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class ObserveHabitsWithTodayStatusUseCase @Inject constructor(
    private val habitRepository: HabitRepository,
    private val completionRepository: CompletionRepository
) {
    operator fun invoke(
        userId: String,
        dateKey: String = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    ): Flow<List<HabitWithStatus>> {
        return combine(
            habitRepository.observeHabitsForUser(userId),
            completionRepository.observeTodayCompletions(userId, dateKey)
        ) { habits, completions ->
            val completedHabitIds = completions.map { it.habitId }.toSet()
            habits.map { habit ->
                HabitWithStatus(
                    habit = habit,
                    isDoneToday = completedHabitIds.contains(habit.id)
                )
            }
        }
    }
}
