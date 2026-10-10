package com.questrings.presentation.home

import com.questrings.domain.model.Habit
import com.questrings.domain.model.HabitCategory
import com.questrings.domain.model.HabitIcon

sealed interface HomeUiEvent {
    data class SelectCategory(val category: HabitCategory?) : HomeUiEvent
    data class ToggleHabit(val habitId: String) : HomeUiEvent
    data class OpenAddHabitSheet(val habitToEdit: Habit? = null) : HomeUiEvent
    data object CloseAddHabitSheet : HomeUiEvent
    data class SaveHabit(
        val title: String,
        val goal: String,
        val category: HabitCategory,
        val icon: HabitIcon
    ) : HomeUiEvent
    data class DeleteHabit(val habitId: String) : HomeUiEvent
    data object DismissToast : HomeUiEvent
    data object DismissLevelUp : HomeUiEvent
    data object DismissConfetti : HomeUiEvent
    data object SignOut : HomeUiEvent
}
