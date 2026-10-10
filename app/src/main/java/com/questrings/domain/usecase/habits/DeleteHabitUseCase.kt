package com.questrings.domain.usecase.habits

import com.questrings.domain.repository.HabitRepository
import javax.inject.Inject

class DeleteHabitUseCase @Inject constructor(
    private val habitRepository: HabitRepository
) {
    suspend operator fun invoke(habitId: String): Result<Unit> {
        return habitRepository.deleteHabit(habitId)
    }
}
