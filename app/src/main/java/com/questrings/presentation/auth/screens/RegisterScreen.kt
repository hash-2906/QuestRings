package com.questrings.presentation.auth.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.questrings.core.designsystem.components.QuestButton
import com.questrings.core.designsystem.components.QuestButtonVariant
import com.questrings.core.designsystem.theme.BackgroundPrimary
import com.questrings.core.designsystem.theme.BorderSubtle
import com.questrings.core.designsystem.theme.DisciplineBlue
import com.questrings.core.designsystem.theme.QuestRingsTheme
import com.questrings.core.designsystem.theme.QuestTheme
import com.questrings.core.designsystem.theme.TextMuted
import com.questrings.core.designsystem.theme.TextPrimary
import com.questrings.core.designsystem.theme.TextSecondary
import com.questrings.core.designsystem.theme.VitalityRed
import com.questrings.presentation.auth.AuthUiEvent
import com.questrings.presentation.auth.AuthUiState
import com.questrings.presentation.auth.AuthViewModel
import com.questrings.presentation.auth.PasswordStrength
import com.questrings.presentation.auth.components.AuthScaffold
import com.questrings.presentation.auth.components.GoogleSignInButton
import com.questrings.presentation.auth.components.PasswordStrengthMeter
import kotlinx.coroutines.flow.collectLatest

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    uiState: AuthUiState,
    onNavigateToLogin: () -> Unit,
    onNavigateToProfileSetup: () -> Unit,
    onGoogleSignInClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is AuthUiEvent.NavigateToProfileSetup -> onNavigateToProfileSetup()
                is AuthUiEvent.NavigateToHome -> onNavigateToProfileSetup()
                is AuthUiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    Scaffold(
        modifier = modifier,
        containerColor = BackgroundPrimary,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        RegisterScreenContent(
            uiState = uiState,
            onEmailChange = viewModel::onEmailChange,
            onEmailBlur = viewModel::onEmailBlur,
            onPasswordChange = viewModel::onPasswordChange,
            onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
            onRegisterClick = viewModel::registerWithEmail,
            onGoogleSignInClick = onGoogleSignInClick,
            onNavigateToLogin = onNavigateToLogin,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun RegisterScreenContent(
    uiState: AuthUiState,
    onEmailChange: (String) -> Unit,
    onEmailBlur: () -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onRegisterClick: () -> Unit,
    onGoogleSignInClick: () -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = Color(0xFF0E0E12),
        unfocusedContainerColor = Color(0xFF0E0E12),
        disabledContainerColor = Color(0xFF0E0E12).copy(alpha = 0.5f),
        focusedBorderColor = DisciplineBlue,
        unfocusedBorderColor = Color(0xFF34343C),
        errorBorderColor = VitalityRed,
        focusedTextColor = TextPrimary,
        unfocusedTextColor = TextPrimary,
        focusedLabelColor = DisciplineBlue,
        unfocusedLabelColor = TextSecondary,
        cursorColor = DisciplineBlue
    )

    AuthScaffold(
        modifier = modifier,
        footer = {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already an adventurer? ",
                    style = QuestTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Text(
                    text = "Sign in",
                    style = QuestTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = DisciplineBlue,
                    modifier = Modifier.clickable(onClick = onNavigateToLogin)
                )
            }
        }
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(13.dp)) {
            Text(
                text = "Begin Your Legend",
                style = QuestTheme.typography.headlineMedium,
                color = TextPrimary
            )
            Text(
                text = "Forge your account to level up, build streaks, and conquer goals.",
                style = QuestTheme.typography.bodyMedium,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Email Field
            Column {
                OutlinedTextField(
                    value = uiState.email,
                    onValueChange = onEmailChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { focusState ->
                            if (!focusState.isFocused) {
                                onEmailBlur()
                            }
                        },
                    label = { Text("Email Address") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    isError = uiState.emailError != null,
                    singleLine = true,
                    enabled = !uiState.isLoading && !uiState.isGoogleLoading,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors
                )

                AnimatedVisibility(
                    visible = uiState.emailError != null,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    uiState.emailError?.let {
                        Text(
                            text = it,
                            style = QuestTheme.typography.bodySmall,
                            color = VitalityRed,
                            modifier = Modifier.padding(start = 6.dp, top = 4.dp)
                        )
                    }
                }
            }

            // Password Field
            Column {
                OutlinedTextField(
                    value = uiState.password,
                    onValueChange = onPasswordChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Password (min 6 characters)") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    isError = uiState.passwordError != null,
                    singleLine = true,
                    enabled = !uiState.isLoading && !uiState.isGoogleLoading,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    ),
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors
                )

                // Password strength meter
                PasswordStrengthMeter(
                    strength = uiState.passwordStrength,
                    modifier = Modifier.padding(top = 6.dp)
                )

                AnimatedVisibility(
                    visible = uiState.passwordError != null,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    uiState.passwordError?.let {
                        Text(
                            text = it,
                            style = QuestTheme.typography.bodySmall,
                            color = VitalityRed,
                            modifier = Modifier.padding(start = 6.dp, top = 4.dp)
                        )
                    }
                }
            }

            // Confirm Password Field
            Column {
                OutlinedTextField(
                    value = uiState.confirmPassword,
                    onValueChange = onConfirmPasswordChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Confirm Password") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password",
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    },
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    isError = uiState.confirmPasswordError != null,
                    singleLine = true,
                    enabled = !uiState.isLoading && !uiState.isGoogleLoading,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = { onRegisterClick() }),
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors
                )

                AnimatedVisibility(
                    visible = uiState.confirmPasswordError != null,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    uiState.confirmPasswordError?.let {
                        Text(
                            text = it,
                            style = QuestTheme.typography.bodySmall,
                            color = VitalityRed,
                            modifier = Modifier.padding(start = 6.dp, top = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Primary CTA Button
            QuestButton(
                text = if (uiState.isLoading) "" else "Create Account",
                onClick = onRegisterClick,
                modifier = Modifier.fillMaxWidth(),
                variant = QuestButtonVariant.Primary,
                enabled = !uiState.isLoading && !uiState.isGoogleLoading,
                shape = RoundedCornerShape(12.dp),
                leadingIcon = {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    }
                }
            )

            // Divider "or"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = BorderSubtle)
                Text(
                    text = "  or  ",
                    style = QuestTheme.typography.monoKicker,
                    color = TextMuted
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = BorderSubtle)
            }

            // Google Sign-In Button
            GoogleSignInButton(
                onClick = onGoogleSignInClick,
                isLoading = uiState.isGoogleLoading,
                enabled = !uiState.isLoading && !uiState.isGoogleLoading,
                text = "Sign up with Google"
            )
        }
    }
}

@Preview(name = "Register Screen - Dark", showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun RegisterScreenDarkPreview() {
    QuestRingsTheme {
        RegisterScreenContent(
            uiState = AuthUiState(
                email = "adventurer@questrings.app",
                passwordStrength = PasswordStrength.MEDIUM
            ),
            onEmailChange = {},
            onEmailBlur = {},
            onPasswordChange = {},
            onConfirmPasswordChange = {},
            onRegisterClick = {},
            onGoogleSignInClick = {},
            onNavigateToLogin = {}
        )
    }
}

@Preview(name = "Register Screen - Light Forced", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun RegisterScreenLightPreview() {
    QuestRingsTheme {
        RegisterScreenContent(
            uiState = AuthUiState(
                email = "adventurer@questrings.app",
                passwordStrength = PasswordStrength.MEDIUM
            ),
            onEmailChange = {},
            onEmailBlur = {},
            onPasswordChange = {},
            onConfirmPasswordChange = {},
            onRegisterClick = {},
            onGoogleSignInClick = {},
            onNavigateToLogin = {}
        )
    }
}

