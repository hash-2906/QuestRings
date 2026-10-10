package com.questrings.data.mapper

import com.questrings.data.local.entity.DailyProgressEntity
import com.questrings.data.local.entity.UserStatsEntity
import com.questrings.domain.model.DailyProgress
import com.questrings.domain.model.HabitCategory
import com.questrings.domain.model.UserStats

fun UserStatsEntity.toDomain(): UserStats {
    val xpMap = mapOf(
        HabitCategory.VITALITY to xpVitality,
        HabitCategory.INTELLECT to xpIntellect,
        HabitCategory.DISCIPLINE to xpDiscipline
    )

    return UserStats(
        userId = userId,
        xp = xp,
        xpWeekly = xpWeekly,
        xpMonthly = xpMonthly,
        level = level,
        currentStreak = currentStreak,
        longestStreak = longestStreak,
        lastActivityDate = lastActivityDate,
        totalHabitsCompleted = totalHabitsCompleted,
        xpByCategory = xpMap
    )
}

fun UserStats.toEntity(isSynced: Boolean = false): UserStatsEntity {
    return UserStatsEntity(
        userId = userId,
        xp = xp,
        xpWeekly = xpWeekly,
        xpMonthly = xpMonthly,
        level = level,
        currentStreak = currentStreak,
        longestStreak = longestStreak,
        lastActivityDate = lastActivityDate,
        totalHabitsCompleted = totalHabitsCompleted,
        xpVitality = xpByCategory[HabitCategory.VITALITY] ?: 0,
        xpIntellect = xpByCategory[HabitCategory.INTELLECT] ?: 0,
        xpDiscipline = xpByCategory[HabitCategory.DISCIPLINE] ?: 0,
        isSynced = isSynced
    )
}

fun DailyProgressEntity.toDomain(): DailyProgress {
    return DailyProgress(
        dateKey = dateKey,
        completionPercent = completionPercent,
        habitsCompleted = habitsCompleted,
        habitsTotal = habitsTotal
    )
}

fun DailyProgress.toEntity(userId: String, isSynced: Boolean = false): DailyProgressEntity {
    return DailyProgressEntity(
        id = "${userId}_$dateKey",
        userId = userId,
        dateKey = dateKey,
        completionPercent = completionPercent,
        habitsCompleted = habitsCompleted,
        habitsTotal = habitsTotal,
        isSynced = isSynced
    )
}
