package com.questrings.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.questrings.core.designsystem.theme.BackgroundPrimary
import com.questrings.core.designsystem.theme.BorderDark
import com.questrings.core.designsystem.theme.QuestRingsTheme
import com.questrings.core.designsystem.theme.RoyalPurple
import com.questrings.core.designsystem.theme.SurfaceDark
import com.questrings.core.designsystem.theme.TextMuted
import com.questrings.core.designsystem.theme.TextPrimary
import com.questrings.core.designsystem.theme.VitalityRed
import com.questrings.core.haptics.rememberQuestHaptics
import com.questrings.domain.config.MedalThreshold
import com.questrings.domain.model.User
import com.questrings.domain.model.medal.MedalTier
import com.questrings.presentation.profile.components.MedalProgressCard
import com.questrings.presentation.profile.components.MedalTimelineRow
import com.questrings.presentation.profile.components.ProfileHeaderCard
import com.questrings.presentation.profile.components.XpProgressSection

@Composable
fun ProfileScreen(
    onNavigateToXpLedger: () -> Unit,
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val haptics = rememberQuestHaptics()

    // Once account deletion finishes, return to login/auth flow
    LaunchedEffect(uiState.isAccountDeleted) {
        if (uiState.isAccountDeleted) {
            haptics.heavySuccess()
            onLogout()
        }
    }

    LaunchedEffect(uiState.deleteAccountError) {
        if (uiState.deleteAccountError != null) {
            haptics.errorImpact()
        }
    }

    ProfileContent(
        uiState = uiState,
        onNavigateToXpLedger = {
            haptics.lightImpact()
            onNavigateToXpLedger()
        },
        onLogout = {
            haptics.lightImpact()
            viewModel.logout()
            onLogout()
        },
        onDeleteAccount = { password ->
            viewModel.deleteAccount(password)
        },
        onDismissDeleteError = {
            viewModel.dismissDeleteError()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileContent(
    uiState: ProfileUiState,
    onNavigateToXpLedger: () -> Unit,
    onLogout: () -> Unit,
    onDeleteAccount: (String?) -> Unit = {},
    onDismissDeleteError: () -> Unit = {}
) {
    val haptics = rememberQuestHaptics()
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var passwordInput by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "COMMAND PROFILE",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        ),
                        color = TextPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundPrimary
                )
            )
        },
        containerColor = BackgroundPrimary
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = RoyalPurple)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    ProfileHeaderCard(
                        user = uiState.user,
                        level = uiState.stats?.level ?: 1,
                        currentMedal = uiState.currentMedal
                    )
                }

                item {
                    MedalProgressCard(
                        currentXp = uiState.totalXp,
                        currentLevel = uiState.stats?.level ?: 1,
                        nextThreshold = uiState.nextMedalThreshold,
                        progress = uiState.progressToNextMedal
                    )
                }

                item {
                    MedalTimelineRow(currentMedal = uiState.currentMedal)
                }

                item {
                    XpProgressSection(
                        level = uiState.stats?.level ?: 1,
                        currentLevelXp = uiState.currentLevelXp,
                        requiredLevelXp = uiState.requiredLevelXp,
                        totalXp = uiState.totalXp,
                        onViewXpLedger = onNavigateToXpLedger
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = onLogout,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
                    ) {
                        Text(
                            text = "LOGOUT OF SYSTEM",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = TextPrimary
                        )
                    }
                }

                // DANGER ZONE (Play Store Mandatory Account Deletion Flow)
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(SurfaceDark)
                            .border(1.dp, VitalityRed.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "DANGER ZONE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
                                ),
                                color = VitalityRed
                            )
                            Text(
                                text = "Irrevocably erase your profile, Room chamber memberships, habit records, XP progress, and cloud credentials.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted,
                                lineHeight = 16.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedButton(
                                onClick = {
                                    haptics.lightImpact()
                                    showDeleteConfirmDialog = true
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, VitalityRed)
                            ) {
                                Text(
                                    text = "DELETE ACCOUNT & ERASE DATA",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 0.8.sp
                                    ),
                                    color = VitalityRed
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }

    // Re-authentication Confirmation Dialog
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = {
                if (!uiState.isDeletingAccount) {
                    showDeleteConfirmDialog = false
                    passwordInput = ""
                }
            },
            title = {
                Text(
                    text = "Confirm Account Deletion",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = VitalityRed
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "This action is permanent and cannot be undone. All personal habits, quest logs, and medals will be completely purged from Firebase and Room storage.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )

                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = { passwordInput = it },
                        label = { Text("Enter password to confirm") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VitalityRed,
                            unfocusedBorderColor = BorderDark,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (uiState.deleteAccountError != null) {
                        Text(
                            text = uiState.deleteAccountError,
                            style = MaterialTheme.typography.bodySmall,
                            color = VitalityRed
                        )
                    }
                }
            },
            confirmButton = {
                OutlinedButton(
                    onClick = {
                        haptics.errorImpact()
                        onDeleteAccount(passwordInput.ifBlank { null })
                    },
                    enabled = !uiState.isDeletingAccount,
                    border = androidx.compose.foundation.BorderStroke(1.dp, VitalityRed),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    if (uiState.isDeletingAccount) {
                        CircularProgressIndicator(
                            color = VitalityRed,
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Delete Everything", color = VitalityRed, fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmDialog = false
                        passwordInput = ""
                        onDismissDeleteError()
                    },
                    enabled = !uiState.isDeletingAccount
                ) {
                    Text("Cancel", color = TextMuted)
                }
            },
            containerColor = SurfaceDark,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    QuestRingsTheme {
        ProfileContent(
            uiState = ProfileUiState(
                isLoading = false,
                user = User(
                    uid = "1",
                    email = "player@questrings.io",
                    displayName = "NeonDrifter",
                    avatarColorHex = "#7E5DFF",
                    createdAt = 0L
                ),
                currentMedal = MedalTier.SILVER,
                nextMedalThreshold = MedalThreshold(
                    tier = MedalTier.GOLD,
                    requiredXp = 7500,
                    requiredLevel = 20,
                    title = "Gold Guardian",
                    description = "Few reach this height. You are among them."
                ),
                progressToNextMedal = 0.65f,
                currentLevelXp = 350,
                requiredLevelXp = 500,
                levelProgress = 0.7f,
                totalXp = 4850,
                streak = 12
            ),
            onNavigateToXpLedger = {},
            onLogout = {}
        )
    }
}
