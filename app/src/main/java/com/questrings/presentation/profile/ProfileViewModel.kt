package com.questrings.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.questrings.domain.config.MedalProgressionConfig
import com.questrings.domain.repository.AuthRepository
import com.questrings.domain.repository.MedalRepository
import com.questrings.domain.repository.StatsRepository
import com.questrings.domain.usecase.auth.DeleteAccountUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private data class AccountActionState(
    val isDeleting: Boolean = false,
    val deleteError: String? = null,
    val isDeleted: Boolean = false
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val statsRepository: StatsRepository,
    private val medalRepository: MedalRepository,
    private val deleteAccountUseCase: DeleteAccountUseCase
) : ViewModel() {

    private val actionState = MutableStateFlow(AccountActionState())

    val uiState: StateFlow<ProfileUiState> = authRepository.currentUser.flatMapLatest { user ->
        if (user == null) {
            combine(actionState) { action ->
                ProfileUiState(
                    isLoading = false,
                    isDeletingAccount = action.isDeleting,
                    deleteAccountError = action.deleteError,
                    isAccountDeleted = action.isDeleted
                )
            }
        } else {
            val statsFlow = statsRepository.observeUserStats(user.uid)
            val medalFlow = medalRepository.observeCurrentMedal(user.uid)

            combine(statsFlow, medalFlow, actionState) { stats, currentMedal, action ->
                val nextThreshold = MedalProgressionConfig.nextThreshold(stats.xp, stats.level)

                val progressToNextMedal = if (nextThreshold == null) {
                    1.0f
                } else {
                    val prevThresholdXp = MedalProgressionConfig.thresholds
                        .filter { it.requiredXp < nextThreshold.requiredXp }
                        .maxByOrNull { it.requiredXp }?.requiredXp ?: 0
                    val totalSpan = (nextThreshold.requiredXp - prevThresholdXp).coerceAtLeast(1)
                    val currentSpan = (stats.xp - prevThresholdXp).coerceAtLeast(0)
                    (currentSpan.toFloat() / totalSpan.toFloat()).coerceIn(0f, 1f)
                }

                val currentLevelXp = stats.xp % 500
                val levelProgress = (currentLevelXp.toFloat() / 500f).coerceIn(0f, 1f)

                ProfileUiState(
                    isLoading = false,
                    user = user,
                    stats = stats,
                    currentMedal = currentMedal,
                    nextMedalThreshold = nextThreshold,
                    progressToNextMedal = progressToNextMedal,
                    currentLevelXp = currentLevelXp,
                    requiredLevelXp = 500,
                    levelProgress = levelProgress,
                    totalXp = stats.xp,
                    streak = stats.currentStreak,
                    isDeletingAccount = action.isDeleting,
                    deleteAccountError = action.deleteError,
                    isAccountDeleted = action.isDeleted
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ProfileUiState()
    )

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }

    fun deleteAccount(password: String?) {
        viewModelScope.launch {
            actionState.update { it.copy(isDeleting = true, deleteError = null) }
            val result = deleteAccountUseCase(password)
            if (result.isSuccess) {
                actionState.update { it.copy(isDeleting = false, isDeleted = true) }
            } else {
                actionState.update {
                    it.copy(
                        isDeleting = false,
                        deleteError = result.exceptionOrNull()?.message ?: "Failed to delete account."
                    )
                }
            }
        }
    }

    fun dismissDeleteError() {
        actionState.update { it.copy(deleteError = null) }
    }
}
