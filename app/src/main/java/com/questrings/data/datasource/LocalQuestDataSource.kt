package com.questrings.data.datasource

import com.questrings.domain.model.Habit
import com.questrings.domain.model.RingCategory
import com.questrings.domain.model.Task
import com.questrings.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalQuestDataSource @Inject constructor() {

    private val initialHabits = listOf(
        Habit(
            id = "water",
            category = RingCategory.Vitality,
            title = "Hydrate",
            goal = "Drink 3L water",
            iconName = "Droplets",
            isDone = false
        ),
        Habit(
            id = "workout",
            category = RingCategory.Vitality,
            title = "Move your body",
            goal = "45-min workout",
            iconName = "Dumbbell",
            isDone = false
        ),
        Habit(
            id = "pages",
            category = RingCategory.Intellect,
            title = "Read & grow",
            goal = "Read 20 pages",
            iconName = "BookOpen",
            isDone = false
        ),
        Habit(
            id = "focus",
            category = RingCategory.Intellect,
            title = "Deep focus",
            goal = "60-min distraction free",
            iconName = "Brain",
            isDone = false
        ),
        Habit(
            id = "morning",
            category = RingCategory.Discipline,
            title = "Morning ritual",
            goal = "Complete your routine",
            iconName = "Sparkles",
            isDone = false
        ),
        Habit(
            id = "sleep",
            category = RingCategory.Discipline,
            title = "Sleep on time",
            goal = "In bed by 11:00 PM",
            iconName = "Moon",
            isDone = false
        )
    )

    private val _habits = MutableStateFlow(initialHabits)
    val habits: Flow<List<Habit>> = _habits.asStateFlow()

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: Flow<List<Task>> = _tasks.asStateFlow()

    private val _userProfile = MutableStateFlow(
        UserProfile(
            level = 7,
            rankTitle = "Guardian",
            currentXp = 540,
            xpForNextLevel = 1000,
            streakDays = 12,
            weeklyHistory = listOf(45, 75, 100, 62, 82, 100, 0)
        )
    )
    val userProfile: Flow<UserProfile> = _userProfile.asStateFlow()

    fun toggleHabit(id: String) {
        val currentList = _habits.value
        val target = currentList.find { it.id == id } ?: return
        val newDone = !target.isDone

        val updatedList = currentList.map { habit ->
            if (habit.id == id) habit.copy(isDone = newDone) else habit
        }
        _habits.value = updatedList

        // XP Calculation matching web logic (+25 XP per habit, +100 bonus on ring completion)
        val deltaXp = if (newDone) 25 else -25
        _userProfile.update { current ->
            var newXp = (current.currentXp + deltaXp).coerceAtLeast(0)
            var newLevel = current.level
            if (newXp >= current.xpForNextLevel) {
                newXp -= current.xpForNextLevel
                newLevel += 1
            }
            current.copy(currentXp = newXp, level = newLevel)
        }
    }

    fun addHabit(habit: Habit) {
        _habits.update { it + habit }
    }

    fun resetDay() {
        _habits.update { list ->
            list.map { it.copy(isDone = false) }
        }
    }

    fun toggleTask(id: String) {
        _tasks.update { list ->
            list.map { if (it.id == id) it.copy(isDone = !it.isDone) else it }
        }
    }

    fun addTask(task: Task) {
        _tasks.update { it + task }
    }

    fun deleteTask(id: String) {
        _tasks.update { list -> list.filterNot { it.id == id } }
    }
}
