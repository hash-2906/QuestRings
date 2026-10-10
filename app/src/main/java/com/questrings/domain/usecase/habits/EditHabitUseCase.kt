package com.questrings.domain.usecase.habits

import com.questrings.domain.model.Habit
import com.questrings.domain.repository.HabitRepository
import javax.inject.Inject

class EditHabitUseCase @Inject constructor(
    private val habitRepository: HabitRepository
) {
    suspend operator fun invoke(habit: Habit): Result<Unit> {
        return habitRepository.updateHabit(habit)
    }
}
