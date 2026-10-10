package com.questrings.presentation.rooms.join

import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.questrings.core.designsystem.components.QuestButton
import com.questrings.core.designsystem.components.QuestButtonVariant
import com.questrings.core.designsystem.theme.AccentGreen
import com.questrings.core.designsystem.theme.AccentPurple
import com.questrings.core.designsystem.theme.BackgroundCard
import com.questrings.core.designsystem.theme.BackgroundPrimary
import com.questrings.core.designsystem.theme.BorderCard
import com.questrings.core.designsystem.theme.BorderSubtle
import com.questrings.core.designsystem.theme.QuestRingsTheme
import com.questrings.core.designsystem.theme.TextMuted
import com.questrings.core.designsystem.theme.TextPrimary
import com.questrings.core.designsystem.theme.TextSecondary
import com.questrings.domain.model.room.Room
import com.questrings.domain.model.room.RoomPrivacy
import com.questrings.presentation.rooms.join.components.OtpCodeInput
import com.questrings.presentation.rooms.join.components.RoomPreviewCard

@Composable
fun JoinRoomScreen(
    onNavigateBack: () -> Unit,
    onNavigateToRoom: (String) -> Unit,
    viewModel: JoinRoomViewModel = hiltViewModel(),
    initialCode: String? = null
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(initialCode) {
        if (!initialCode.isNullOrBlank()) {
            viewModel.onCodeChange(initialCode)
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.navigationEffect.collect { effect ->
            when (effect) {
                is JoinRoomNavigationEffect.NavigateToRoom -> onNavigateToRoom(effect.roomId)
                JoinRoomNavigationEffect.NavigateBack -> onNavigateBack()
            }
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.dismissError()
        }
    }

    Scaffold(
        containerColor = BackgroundPrimary,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }
                Text(
                    text = "JOIN QUEST ROOM",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Subtitle instructions
            Text(
                text = "Enter Room Join Code",
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Enter the 6-character code provided by your party leader or room administrator.",
                color = TextSecondary,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 6-Character OTP Input
            OtpCodeInput(
                code = uiState.code,
                onCodeChange = viewModel::onCodeChange,
                onComplete = { code ->
                    viewModel.lookupRoom(code)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Quick Paste Action Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                QuestButton(
                    text = "Paste From Clipboard",
                    icon = Icons.Default.ContentPaste,
                    variant = QuestButtonVariant.Ghost,
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                        val clip = clipboard?.primaryClip?.getItemAt(0)?.text?.toString()
                        if (!clip.isNullOrBlank()) {
                            // Strip deep link prefix if user copied full url
                            val sanitized = clip.replace("questrings://join/", "").trim().take(6)
                            viewModel.onCodeChange(sanitized)
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Loading Lookup Indicator
            if (uiState.isLookingUp) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = AccentPurple,
                        strokeWidth = 2.dp
                    )
                    Text(
                        text = "Searching Quest Chambers...",
                        color = TextMuted,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Preview Room Card when found
            AnimatedVisibility(
                visible = uiState.previewRoom != null && !uiState.isLookingUp,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                uiState.previewRoom?.let { room ->
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        RoomPreviewCard(room = room)

                        Spacer(modifier = Modifier.height(24.dp))

                        val isRoomFull = room.memberCount >= room.maxMembers

                        if (uiState.joinStatus == JoinStatusType.REQUEST_SUBMITTED) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(BackgroundCard)
                                    .border(1.dp, AccentGreen.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = AccentGreen,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Column {
                                        Text(
                                            text = "REQUEST SUBMITTED",
                                            color = AccentGreen,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                        Text(
                                            text = "Awaiting approval from the room owner.",
                                            color = TextSecondary,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        } else {
                            QuestButton(
                                text = when {
                                    isRoomFull -> "ROOM AT CAPACITY"
                                    room.privacy == RoomPrivacy.PUBLIC -> "JOIN ROOM NOW"
                                    else -> "REQUEST TO JOIN ROOM"
                                },
                                enabled = !isRoomFull && !uiState.isLoading,
                                isLoading = uiState.isLoading,
                                variant = QuestButtonVariant.Primary,
                                modifier = Modifier.fillMaxWidth(),
                                onClick = viewModel::joinRoom
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun JoinRoomScreenPreview() {
    QuestRingsTheme {
        JoinRoomScreen(
            onNavigateBack = {},
            onNavigateToRoom = {},
            initialCode = "X7K2QR"
        )
    }
}
