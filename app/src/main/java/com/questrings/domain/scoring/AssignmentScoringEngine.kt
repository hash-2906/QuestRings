package com.questrings.domain.scoring

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AssignmentScoringEngine @Inject constructor() {

    fun computeBasePoints(rank: Int, isLate: Boolean): Int {
        val base = when (rank) {
            1 -> 100
            2 -> 85
            3 -> 72
            4 -> 61
            5 -> 52
            else -> maxOf(10, 52 - (rank - 5) * 4)
        }
        return if (isLate) (base * 0.5f).toInt() else base
    }

    fun computeSpeedBonus(completedAt: Long, createdAt: Long, dueAt: Long, isLate: Boolean = false): Int {
        if (isLate || dueAt <= createdAt) return 0
        val totalWindow = (dueAt - createdAt).toFloat()
        val timeUsed = (completedAt - createdAt).toFloat()
        val fractionUsed = (timeUsed / totalWindow).coerceIn(0f, 1f)

        return when {
            fractionUsed <= 0.25f -> 25
            fractionUsed <= 0.50f -> 15
            fractionUsed <= 0.75f -> 5
            else -> 0
        }
    }

    fun computeTotalPoints(rank: Int, completedAt: Long, createdAt: Long, dueAt: Long, isLate: Boolean): Pair<Int, Int> {
        val base = computeBasePoints(rank, isLate)
        val speed = computeSpeedBonus(completedAt, createdAt, dueAt, isLate)
        val total = base + speed
        return Pair(total, speed)
    }

    fun computeRoomAssignmentXp(points: Int): Int =
        (points * 1.5f).toInt()
}
