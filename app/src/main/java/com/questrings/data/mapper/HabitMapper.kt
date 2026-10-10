package com.questrings.data.mapper

import com.questrings.data.local.entity.HabitCompletionEntity
import com.questrings.data.local.entity.HabitEntity
import com.questrings.domain.model.Habit
import com.questrings.domain.model.HabitCategory
import com.questrings.domain.model.HabitCompletion
import com.questrings.domain.model.HabitIcon

fun HabitEntity.toDomain(): Habit {
    return Habit(
        id = id,
        userId = userId,
        title = title,
        goal = goal,
        category = try { HabitCategory.valueOf(category) } catch (e: Exception) { HabitCategory.VITALITY },
        icon = try { HabitIcon.valueOf(icon) } catch (e: Exception) { HabitIcon.TARGET },
        isDefault = isDefault,
        createdAt = createdAt,
        sortOrder = sortOrder
    )
}

fun Habit.toEntity(isSynced: Boolean = false): HabitEntity {
    return HabitEntity(
        id = id,
        userId = userId,
        title = title,
        goal = goal,
        category = category.name,
        icon = icon.name,
        isDefault = isDefault,
        createdAt = createdAt,
        sortOrder = sortOrder,
        isSynced = isSynced
    )
}

fun HabitCompletionEntity.toDomain(): HabitCompletion {
    return HabitCompletion(
        id = id,
        habitId = habitId,
        userId = userId,
        completedAt = completedAt,
        dateKey = dateKey
    )
}

fun HabitCompletion.toEntity(isSynced: Boolean = false): HabitCompletionEntity {
    return HabitCompletionEntity(
        id = id,
        habitId = habitId,
        userId = userId,
        completedAt = completedAt,
        dateKey = dateKey,
        isSynced = isSynced
    )
}
