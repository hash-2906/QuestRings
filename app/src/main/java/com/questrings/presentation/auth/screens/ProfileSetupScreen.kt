package com.questrings.presentation.auth.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.questrings.core.designsystem.components.QuestButton
import com.questrings.core.designsystem.components.QuestButtonVariant
import com.questrings.core.designsystem.theme.BackgroundCard
import com.questrings.core.designsystem.theme.BackgroundPrimary
import com.questrings.core.designsystem.theme.BorderCard
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
import com.questrings.presentation.auth.components.AuthScaffold
import com.questrings.presentation.auth.components.AvatarColorPicker
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ProfileSetupScreen(
    viewModel: AuthViewModel,
    uiState: AuthUiState,
    onNavigateToHome: () -> Unit,
    onNavigateBackToAuth: () -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is AuthUiEvent.NavigateToHome -> onNavigateToHome()
                is AuthUiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
                else -> {}
            }
        }
    }

    Scaffold(
        modifier = modifier,
        containerColor = BackgroundPrimary,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        ProfileSetupScreenContent(
            uiState = uiState,
            onDisplayNameChange = viewModel::onDisplayNameChange,
            onAvatarColorSelect = viewModel::onAvatarColorSelect,
            onSubmitProfile = viewModel::saveProfile,
            onSignOutAndLeave = {
                viewModel.signOutAndLeaveSetup()
                onNavigateBackToAuth()
            },
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun ProfileSetupScreenContent(
    uiState: AuthUiState,
    onDisplayNameChange: (String) -> Unit,
    onAvatarColorSelect: (String) -> Unit,
    onSubmitProfile: () -> Unit,
    onSignOutAndLeave: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showLeaveDialog by remember { mutableStateOf(false) }

    // Intercept hardware / gesture back press
    BackHandler {
        showLeaveDialog = true
    }

    if (showLeaveDialog) {
        AlertDialog(
            onDismissRequest = { showLeaveDialog = false },
            title = {
                Text(
                    text = "Leave setup?",
                    style = QuestTheme.typography.titleMedium,
                    color = TextPrimary
                )
            },
            text = {
                Text(
                    text = "Your account was created but your hero profile won't be saved. Are you sure you want to sign out?",
                    style = QuestTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            },
            containerColor = BackgroundCard,
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier.border(1.dp, BorderCard, RoundedCornerShape(18.dp)),
            confirmButton = {
                TextButton(
                    onClick = {
                        showLeaveDialog = false
                        onSignOutAndLeave()
                    }
                ) {
                    Text("Sign out & Leave", color = VitalityRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLeaveDialog = false }) {
                    Text("Stay", color = DisciplineBlue, fontWeight = FontWeight.SemiBold)
                }
            }
        )
    }

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

    AuthScaffold(modifier = modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Who are you, adventurer?",
                    style = QuestTheme.typography.headlineMedium.copy(fontSize = 20.sp),
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Customize your avatar and legend name.",
                    style = QuestTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }

            // Avatar Color Picker + Live Preview
            AvatarColorPicker(
                selectedColorHex = uiState.selectedAvatarColorHex,
                onColorSelected = onAvatarColorSelect,
                displayName = uiState.displayName
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Username input
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = uiState.displayName,
                    onValueChange = onDisplayNameChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Pick your legend name") },
                    placeholder = { Text("e.g. ShadowKnight", color = TextMuted) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Badge,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        Text(
                            text = "${uiState.displayName.length}/20",
                            style = QuestTheme.typography.monoKicker,
                            color = TextMuted,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                    },
                    isError = uiState.displayNameError != null,
                    singleLine = true,
                    enabled = !uiState.isProfileSetupLoading,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { onSubmitProfile() }),
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors
                )

                AnimatedVisibility(
                    visible = uiState.displayNameError != null,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    uiState.displayNameError?.let {
                        Text(
                            text = it,
                            style = QuestTheme.typography.bodySmall,
                            color = VitalityRed,
                            modifier = Modifier.padding(start = 6.dp, top = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Submit Button
            QuestButton(
                text = if (uiState.isProfileSetupLoading) "" else "Begin Quest",
                onClick = onSubmitProfile,
                modifier = Modifier.fillMaxWidth(),
                variant = QuestButtonVariant.Primary,
                enabled = !uiState.isProfileSetupLoading,
                shape = RoundedCornerShape(12.dp),
                trailingIcon = {
                    if (!uiState.isProfileSetupLoading) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                },
                leadingIcon = {
                    if (uiState.isProfileSetupLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    }
                }
            )
        }
    }
}

@Preview(name = "Profile Setup - Dark", showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun ProfileSetupScreenDarkPreview() {
    QuestRingsTheme {
        ProfileSetupScreenContent(
            uiState = AuthUiState(
                displayName = "ShadowKnight",
                selectedAvatarColorHex = "#7E5DFF"
            ),
            onDisplayNameChange = {},
            onAvatarColorSelect = {},
            onSubmitProfile = {},
            onSignOutAndLeave = {}
        )
    }
}

@Preview(name = "Profile Setup - Light Forced", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun ProfileSetupScreenLightPreview() {
    QuestRingsTheme {
        ProfileSetupScreenContent(
            uiState = AuthUiState(
                displayName = "ShadowKnight",
                selectedAvatarColorHex = "#7E5DFF"
            ),
            onDisplayNameChange = {},
            onAvatarColorSelect = {},
            onSubmitProfile = {},
            onSignOutAndLeave = {}
        )
    }
}

