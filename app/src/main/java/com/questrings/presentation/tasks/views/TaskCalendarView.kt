package com.questrings.presentation.tasks.views

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.questrings.core.designsystem.theme.QuestTheme
import com.questrings.domain.model.Task
import com.questrings.domain.model.TaskPriority
import com.questrings.presentation.tasks.TaskWithLiveStatus
import com.questrings.presentation.tasks.components.TaskCard
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun TaskCalendarView(
    tasks: List<TaskWithLiveStatus>,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onCompleteTask: (String) -> Unit,
    onDeleteTask: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentYearMonth by remember { mutableStateOf(YearMonth.from(selectedDate)) }
    val zoneId = ZoneId.systemDefault()

    // Map tasks to dates
    val tasksByDate = remember(tasks) {
        tasks.groupBy {
            Instant.ofEpochMilli(it.task.dueAt).atZone(zoneId).toLocalDate()
        }
    }

    val selectedDateTasks = tasksByDate[selectedDate] ?: emptyList()

    Column(modifier = modifier.fillMaxSize()) {
        // Month Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { currentYearMonth = currentYearMonth.minusMonths(1) }) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = "Previous Month",
                    tint = QuestTheme.colors.textPrimary
                )
            }

            Text(
                text = "${currentYearMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${currentYearMonth.year}",
                style = QuestTheme.typography.headingMedium.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = QuestTheme.colors.textPrimary
                )
            )

            IconButton(onClick = { currentYearMonth = currentYearMonth.plusMonths(1) }) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Next Month",
                    tint = QuestTheme.colors.textPrimary
                )
            }
        }

        // Days of week header (M, T, W, T, F, S, S)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            listOf("M", "T", "W", "T", "F", "S", "S").forEach { day ->
                Text(
                    text = day,
                    style = QuestTheme.typography.monoLabel.copy(
                        fontSize = 11.sp,
                        color = QuestTheme.colors.textMuted
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Month Calendar Grid
        MonthCalendarGrid(
            yearMonth = currentYearMonth,
            selectedDate = selectedDate,
            tasksByDate = tasksByDate,
            onDateSelected = onDateSelected,
            modifier = Modifier.padding(horizontal = 18.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Agenda Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(QuestTheme.colors.backgroundSecondary)
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .padding(top = 16.dp)
        ) {
            // Selected Date Sticky Subheader
            val formattedSelected = selectedDate.format(DateTimeFormatter.ofPattern("EEEE, MMM d, yyyy"))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formattedSelected,
                    style = QuestTheme.typography.headingMedium.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = QuestTheme.colors.textPrimary
                    )
                )
                Text(
                    text = "${selectedDateTasks.size} quest${if (selectedDateTasks.size != 1) "s" else ""}",
                    style = QuestTheme.typography.monoLabel.copy(
                        fontSize = 11.sp,
                        color = QuestTheme.colors.disciplineBlue
                    )
                )
            }

            if (selectedDateTasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No quests on this day.",
                            style = QuestTheme.typography.bodyMedium.copy(
                                color = QuestTheme.colors.textMuted
                            )
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(selectedDateTasks, key = { it.task.id }) { item ->
                        TaskCard(
                            taskWithStatus = item,
                            onComplete = { onCompleteTask(item.task.id) },
                            onDelete = { onDeleteTask(item.task) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MonthCalendarGrid(
    yearMonth: YearMonth,
    selectedDate: LocalDate,
    tasksByDate: Map<LocalDate, List<TaskWithLiveStatus>>,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val firstDayOfMonth = yearMonth.atDay(1)
    val dayOfWeekOffset = (firstDayOfMonth.dayOfWeek.value - 1) // 0 for Monday
    val daysInMonth = yearMonth.lengthOfMonth()
    val today = LocalDate.now()

    val totalCells = ((dayOfWeekOffset + daysInMonth + 6) / 7) * 7

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        for (week in 0 until (totalCells / 7)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                for (dayIndex in 0..6) {
                    val cellNumber = week * 7 + dayIndex
                    val dayOfMonth = cellNumber - dayOfWeekOffset + 1

                    if (dayOfMonth in 1..daysInMonth) {
                        val date = yearMonth.atDay(dayOfMonth)
                        val isSelected = date == selectedDate
                        val isToday = date == today
                        val dayTasks = tasksByDate[date] ?: emptyList()

                        CalendarDateCell(
                            day = dayOfMonth,
                            isSelected = isSelected,
                            isToday = isToday,
                            tasks = dayTasks,
                            onClick = { onDateSelected(date) }
                        )
                    } else {
                        Spacer(modifier = Modifier.size(36.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarDateCell(
    day: Int,
    isSelected: Boolean,
    isToday: Boolean,
    tasks: List<TaskWithLiveStatus>,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(
                if (isSelected) Color(0xFF26262D) else Color.Transparent
            )
            .border(
                width = if (isToday) 1.5.dp else 0.dp,
                color = if (isToday) Color.White else Color.Transparent,
                shape = CircleShape
            )
            .clickable { onClick() },
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "$day",
            style = QuestTheme.typography.monoNumber.copy(
                fontSize = 12.sp,
                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) Color.White else QuestTheme.colors.textPrimary
            )
        )

        // Priority dots beneath date number (up to 3)
        if (tasks.isNotEmpty()) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                tasks.take(3).forEach { taskItem ->
                    val dotColor = when (taskItem.task.priority) {
                        TaskPriority.LOW -> QuestTheme.colors.intellectGreen
                        TaskPriority.MEDIUM -> QuestTheme.colors.disciplineBlue
                        TaskPriority.HIGH -> QuestTheme.colors.streakOrange
                        TaskPriority.URGENT -> QuestTheme.colors.vitalityRed
                    }
                    Box(
                        modifier = Modifier
                            .size(3.5.dp)
                            .clip(CircleShape)
                            .background(dotColor)
                    )
                }
            }
        }
    }
}
