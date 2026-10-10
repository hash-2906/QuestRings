package com.questrings.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.questrings.domain.model.User
import com.questrings.domain.usecase.auth.LoginWithEmailUseCase
import com.questrings.domain.usecase.auth.ObserveCurrentUserUseCase
import com.questrings.domain.usecase.auth.RegisterWithEmailUseCase
import com.questrings.domain.usecase.auth.SaveUserProfileUseCase
import com.questrings.domain.usecase.auth.SignInWithGoogleUseCase
import com.questrings.domain.usecase.auth.SignOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val registerWithEmailUseCase: RegisterWithEmailUseCase,
    private val loginWithEmailUseCase: LoginWithEmailUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val saveUserProfileUseCase: SaveUserProfileUseCase,
    private val observeCurrentUserUseCase: ObserveCurrentUserUseCase,
    private val signOutUseCase: SignOutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<AuthUiEvent>()
    val uiEvent: SharedFlow<AuthUiEvent> = _uiEvent.asSharedFlow()

    private var emailHasBeenBlurred = false

    private val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    private val usernameRegex = Regex("^[a-zA-Z0-9_ ]{3,20}$")

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email) }
        if (emailHasBeenBlurred) {
            validateEmail(email)
        }
    }

    fun onEmailBlur() {
        emailHasBeenBlurred = true
        validateEmail(_uiState.value.email)
    }

    private fun validateEmail(email: String) {
        val trimmed = email.trim()
        val error = when {
            trimmed.isEmpty() -> "Email address is required."
            !emailRegex.matches(trimmed) -> "Please enter a valid email address."
            else -> null
        }
        _uiState.update { it.copy(emailError = error) }
    }

    fun onPasswordChange(password: String) {
        val strength = calculatePasswordStrength(password)
        _uiState.update {
            it.copy(
                password = password,
                passwordStrength = strength,
                passwordError = null
            )
        }
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        val error = if (confirmPassword.isNotEmpty() && confirmPassword != _uiState.value.password) {
            "Passwords do not match."
        } else null
        _uiState.update {
            it.copy(
                confirmPassword = confirmPassword,
                confirmPasswordError = error
            )
        }
    }

    fun onDisplayNameChange(name: String) {
        if (name.length <= 20) {
            _uiState.update { it.copy(displayName = name, displayNameError = null) }
        }
    }

    fun onAvatarColorSelect(colorHex: String) {
        _uiState.update { it.copy(selectedAvatarColorHex = colorHex) }
    }

    fun loginWithEmail() {
        val email = _uiState.value.email.trim()
        val password = _uiState.value.password

        validateEmail(email)
        if (_uiState.value.emailError != null) return

        if (password.isEmpty()) {
            _uiState.update { it.copy(passwordError = "Password cannot be empty.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, generalError = null) }
            val result = loginWithEmailUseCase(email, password)
            _uiState.update { it.copy(isLoading = false) }

            result.fold(
                onSuccess = { user ->
                    if (user.displayName.isBlank()) {
                        _uiEvent.emit(AuthUiEvent.NavigateToProfileSetup)
                    } else {
                        _uiEvent.emit(AuthUiEvent.NavigateToHome)
                    }
                },
                onFailure = { error ->
                    val message = error.localizedMessage ?: "Sign in failed"
                    _uiState.update { it.copy(generalError = message) }
                    _uiEvent.emit(AuthUiEvent.ShowSnackbar(message))
                }
            )
        }
    }

    fun registerWithEmail() {
        val email = _uiState.value.email.trim()
        val password = _uiState.value.password
        val confirmPassword = _uiState.value.confirmPassword

        validateEmail(email)
        if (_uiState.value.emailError != null) return

        if (password.length < 6) {
            _uiState.update { it.copy(passwordError = "Password must be at least 6 characters.") }
            return
        }

        if (password != confirmPassword) {
            _uiState.update { it.copy(confirmPasswordError = "Passwords do not match.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, generalError = null) }
            val result = registerWithEmailUseCase(email, password)
            _uiState.update { it.copy(isLoading = false) }

            result.fold(
                onSuccess = {
                    _uiEvent.emit(AuthUiEvent.NavigateToProfileSetup)
                },
                onFailure = { error ->
                    val message = error.localizedMessage ?: "Registration failed"
                    _uiState.update { it.copy(generalError = message) }
                    _uiEvent.emit(AuthUiEvent.ShowSnackbar(message))
                }
            )
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isGoogleLoading = true, generalError = null) }
            val result = signInWithGoogleUseCase(idToken)
            _uiState.update { it.copy(isGoogleLoading = false) }

            result.fold(
                onSuccess = { user ->
                    if (user.displayName.isBlank()) {
                        _uiEvent.emit(AuthUiEvent.NavigateToProfileSetup)
                    } else {
                        _uiEvent.emit(AuthUiEvent.NavigateToHome)
                    }
                },
                onFailure = { error ->
                    val message = error.localizedMessage ?: "Google Sign-In failed"
                    _uiState.update { it.copy(generalError = message) }
                    _uiEvent.emit(AuthUiEvent.ShowSnackbar(message))
                }
            )
        }
    }

    fun saveProfile() {
        val displayName = _uiState.value.displayName.trim()
        if (displayName.isEmpty()) {
            _uiState.update { it.copy(displayNameError = "Please enter a legend name.") }
            return
        }

        if (!usernameRegex.matches(displayName)) {
            _uiState.update {
                it.copy(displayNameError = "Must be 3-20 chars without special characters.")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isProfileSetupLoading = true, generalError = null) }
            val currentUser = observeCurrentUserUseCase().firstOrNull()

            if (currentUser == null) {
                _uiState.update { it.copy(isProfileSetupLoading = false) }
                _uiEvent.emit(AuthUiEvent.ShowSnackbar("Session expired. Please sign in again."))
                return@launch
            }

            val updatedUser = currentUser.copy(
                displayName = displayName,
                avatarColorHex = _uiState.value.selectedAvatarColorHex
            )

            val result = saveUserProfileUseCase(updatedUser)
            _uiState.update { it.copy(isProfileSetupLoading = false) }

            result.fold(
                onSuccess = {
                    _uiEvent.emit(AuthUiEvent.NavigateToHome)
                },
                onFailure = { error ->
                    val message = error.localizedMessage ?: "Failed to save profile."
                    _uiState.update { it.copy(generalError = message) }
                    _uiEvent.emit(AuthUiEvent.ShowSnackbar(message))
                }
            )
        }
    }

    fun signOutAndLeaveSetup() {
        viewModelScope.launch {
            signOutUseCase()
            _uiState.value = AuthUiState()
            emailHasBeenBlurred = false
        }
    }

    fun clearGeneralError() {
        _uiState.update { it.copy(generalError = null) }
    }

    private fun calculatePasswordStrength(password: String): PasswordStrength {
        if (password.isEmpty()) return PasswordStrength.NONE
        if (password.length < 8 || password.all { it.isLetter() }) return PasswordStrength.WEAK

        val hasLower = password.any { it.isLowerCase() }
        val hasUpper = password.any { it.isUpperCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasSpecial = password.any { !it.isLetterOrDigit() }

        val mixedCaseOrDigits = (hasLower && hasUpper) || hasDigit

        return if (password.length >= 10 && hasLower && hasUpper && hasDigit && hasSpecial) {
            PasswordStrength.STRONG
        } else if (password.length >= 8 && mixedCaseOrDigits) {
            PasswordStrength.MEDIUM
        } else {
            PasswordStrength.WEAK
        }
    }
}
