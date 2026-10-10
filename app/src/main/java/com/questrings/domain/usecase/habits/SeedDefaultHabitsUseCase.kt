package com.questrings.domain.usecase.habits

import com.questrings.domain.repository.HabitRepository
import javax.inject.Inject

class SeedDefaultHabitsUseCase @Inject constructor(
    private val habitRepository: HabitRepository
) {
    suspend operator fun invoke(userId: String): Result<Unit> {
        return habitRepository.seedDefaultHabits(userId)
    }
}
