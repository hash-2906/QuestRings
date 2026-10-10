package com.questrings.presentation.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.questrings.domain.model.Task
import com.questrings.domain.model.TaskCountdown
import com.questrings.domain.model.TaskStatus
import com.questrings.domain.usecase.auth.ObserveCurrentUserUseCase
import com.questrings.domain.usecase.tasks.AddTaskUseCase
import com.questrings.domain.usecase.tasks.CompleteTaskUseCase
import com.questrings.domain.usecase.tasks.DeleteTaskUseCase
import com.questrings.domain.usecase.tasks.ObserveTasksWithLiveStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val observeCurrentUserUseCase: ObserveCurrentUserUseCase,
    private val observeTasksWithLiveStatusUseCase: ObserveTasksWithLiveStatusUseCase,
    private val addTaskUseCase: AddTaskUseCase,
    private val completeTaskUseCase: CompleteTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase
) : ViewModel() {

    private val _viewMode = MutableStateFlow(TaskViewMode.LIST)
    private val _filterStatus = MutableStateFlow(TaskStatusFilter.ACTIVE)
    private val _selectedDate = MutableStateFlow(LocalDate.now())
    private val _isAddTaskSheetOpen = MutableStateFlow(false)
    private val _toastMessage = MutableStateFlow<String?>(null)
    private val _snackbarMessage = MutableStateFlow<String?>(null)
    private val _lastDeletedTask = MutableStateFlow<Task?>(null)

    // 1-second ticker without DB polling
    private val tickerFlow = flow {
        while (true) {
            emit(System.currentTimeMillis())
            delay(1000)
        }
    }

    private val currentUserFlow = observeCurrentUserUseCase()

    private val rawTasksFlow = currentUserFlow.flatMapLatest { user ->
        if (user != null) {
            observeTasksWithLiveStatusUseCase(user.uid)
        } else {
            flowOf(emptyList())
        }
    }

    val uiState: StateFlow<TasksUiState> = combine(
        rawTasksFlow,
        tickerFlow,
        _viewMode,
        _filterStatus,
        _selectedDate,
        _isAddTaskSheetOpen,
        _toastMessage,
        _snackbarMessage
    ) { tasks, currentTime, viewMode, filter, selectedDate, isSheetOpen, toast, snackbar ->
        val tasksWithStatus = tasks.map { task ->
            val liveStatus = task.computeLiveStatus(currentTime)
            val countdown = computeCountdown(task, currentTime, liveStatus)
            TaskWithLiveStatus(
                task = task.copy(status = liveStatus),
                liveStatus = liveStatus,
                countdown = countdown
            )
        }

        TasksUiState(
            tasks = tasksWithStatus,
            viewMode = viewMode,
            filterStatus = filter,
            selectedDate = selectedDate,
            isLoading = false,
            isAddTaskSheetOpen = isSheetOpen,
            toastMessage = toast,
            snackbarMessage = snackbar,
            lastDeletedTask = _lastDeletedTask.value
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TasksUiState()
    )

    fun onEvent(event: TasksUiEvent) {
        when (event) {
            is TasksUiEvent.SetViewMode -> _viewMode.value = event.mode
            is TasksUiEvent.SetFilter -> _filterStatus.value = event.filter
            is TasksUiEvent.SelectDate -> _selectedDate.value = event.date
            is TasksUiEvent.OpenAddTaskSheet -> _isAddTaskSheetOpen.value = true
            is TasksUiEvent.CloseAddTaskSheet -> _isAddTaskSheetOpen.value = false
            is TasksUiEvent.CreateTask -> {
                val user = observeCurrentUserUseCase()
                viewModelScope.launch {
                    val uid = user.stateIn(viewModelScope).value?.uid ?: ""
                    val taskId = UUID.randomUUID().toString()
                    val newTask = Task(
                        id = taskId,
                        userId = uid,
                        title = event.title,
                        notes = event.notes,
                        priority = event.priority,
                        status = TaskStatus.UPCOMING,
                        createdAt = System.currentTimeMillis(),
                        dueAt = event.dueAt,
                        reminders = event.reminders.map { it.copy(taskId = taskId) }
                    )
                    addTaskUseCase(newTask, newTask.reminders)
                        .onSuccess {
                            _isAddTaskSheetOpen.value = false
                            _toastMessage.value = "Quest Scheduled!"
                        }
                        .onFailure {
                            _toastMessage.value = it.message ?: "Failed to create quest"
                        }
                }
            }
            is TasksUiEvent.CompleteTask -> {
                viewModelScope.launch {
                    completeTaskUseCase(event.taskId)
                        .onSuccess { xpGained ->
                            val task = uiState.value.tasks.find { it.task.id == event.taskId }
                            val badge = task?.task?.priority?.badgeLabel ?: "QUEST"
                            _toastMessage.value = "+$xpGained XP · $badge COMPLETED"
                        }
                        .onFailure {
                            _toastMessage.value = it.message ?: "Cannot complete quest"
                        }
                }
            }
            is TasksUiEvent.DeleteTask -> {
                _lastDeletedTask.value = event.task
                viewModelScope.launch {
                    deleteTaskUseCase(event.task.id)
                    _snackbarMessage.value = "Quest removed: ${event.task.title}"
                }
            }
            is TasksUiEvent.UndoDeleteTask -> {
                val taskToRestore = _lastDeletedTask.value
                if (taskToRestore != null) {
                    viewModelScope.launch {
                        addTaskUseCase(taskToRestore, taskToRestore.reminders)
                        _lastDeletedTask.value = null
                        _snackbarMessage.value = null
                        _toastMessage.value = "Quest restored"
                    }
                }
            }
            is TasksUiEvent.DismissToast -> _toastMessage.value = null
            is TasksUiEvent.DismissSnackbar -> _snackbarMessage.value = null
        }
    }

    private fun computeCountdown(task: Task, now: Long, liveStatus: TaskStatus): TaskCountdown {
        val totalMs = (task.dueAt - task.createdAt).coerceAtLeast(1000L)
        val remainingMs = task.dueAt - now
        val within24Hours = remainingMs in 0..86_400_000L
        val inGrace = liveStatus == TaskStatus.GRACE_PERIOD

        val progressFraction = when (liveStatus) {
            TaskStatus.COMPLETED -> 1.0f
            TaskStatus.FAILED -> 0.0f
            TaskStatus.GRACE_PERIOD -> 0.0f
            else -> (remainingMs.toFloat() / totalMs.toFloat()).coerceIn(0f, 1f)
        }

        val displayLabel = when (liveStatus) {
            TaskStatus.COMPLETED -> "Completed"
            TaskStatus.FAILED -> "Quest Failed"
            TaskStatus.GRACE_PERIOD -> {
                val graceRemainingMs = (task.dueAt + 86_400_000L) - now
                "GRACE: ${formatHms(graceRemainingMs.coerceAtLeast(0L))}"
            }
            else -> {
                if (within24Hours) {
                    formatHms(remainingMs)
                } else {
                    val days = TimeUnit.MILLISECONDS.toDays(remainingMs)
                    val hours = TimeUnit.MILLISECONDS.toHours(remainingMs) % 24
                    "${days}d ${hours}h left"
                }
            }
        }

        return TaskCountdown(
            totalMs = totalMs,
            remainingMs = remainingMs,
            progressFraction = progressFraction,
            displayLabel = displayLabel,
            isCountingDown = within24Hours,
            isInGrace = inGrace
        )
    }

    private fun formatHms(ms: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(ms)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(ms) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(ms) % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}
