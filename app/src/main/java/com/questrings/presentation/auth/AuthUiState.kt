package com.questrings.presentation.auth

enum class PasswordStrength {
    NONE,
    WEAK,
    MEDIUM,
    STRONG
}

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val passwordStrength: PasswordStrength = PasswordStrength.NONE,
    val isLoading: Boolean = false,
    val isGoogleLoading: Boolean = false,
    val generalError: String? = null,
    // Profile Setup fields
    val displayName: String = "",
    val displayNameError: String? = null,
    val selectedAvatarColorHex: String = "#7E5DFF",
    val isProfileSetupLoading: Boolean = false
)
