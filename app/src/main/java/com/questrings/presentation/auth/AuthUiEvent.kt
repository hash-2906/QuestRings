package com.questrings.presentation.auth

sealed interface AuthUiEvent {
    data object NavigateToHome : AuthUiEvent
    data object NavigateToProfileSetup : AuthUiEvent
    data class ShowSnackbar(val message: String) : AuthUiEvent
}
