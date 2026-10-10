package com.questrings.presentation.assignment.detail

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.questrings.core.designsystem.theme.AccentOrange
import com.questrings.core.designsystem.theme.AccentPurple
import com.questrings.core.designsystem.theme.BackgroundCard
import com.questrings.core.designsystem.theme.BackgroundPrimary
import com.questrings.core.designsystem.theme.BorderCard
import com.questrings.core.designsystem.theme.DisciplineBlue
import com.questrings.core.designsystem.theme.QuestRingsTheme
import com.questrings.core.designsystem.theme.TextMuted
import com.questrings.core.designsystem.theme.TextPrimary
import com.questrings.core.designsystem.theme.TextSecondary
import com.questrings.domain.model.assignment.Assignment
import com.questrings.domain.model.assignment.AssignmentCompletion
import com.questrings.domain.model.assignment.AssignmentStatus
import com.questrings.domain.model.assignment.AssignmentType
import com.questrings.domain.model.assignment.ChecklistItem
import com.questrings.domain.model.assignment.CompletionVerificationStatus
import com.questrings.domain.model.assignment.LeaderboardEntry
import com.questrings.presentation.assignment.detail.components.AssignmentLeaderboardList
import com.questrings.presentation.assignment.detail.components.ChecklistCompletionCard
import com.questrings.presentation.assignment.detail.components.CompletionStatusCard
import com.questrings.presentation.assignment.detail.components.SimpleCompletionCard
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignmentDetailScreen(
    onNavigateBack: () -> Unit,
    viewModel: AssignmentDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.toastMessage) {
        uiState.toastMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.dismissToast()
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
                    .padding(horizontal = 8.dp, vertical = 6.dp),
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
                    text = uiState.assignment?.title ?: "QUEST DETAILS",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    ) { innerPadding ->
        if (uiState.assignment == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = AccentPurple)
            }
        } else {
            val assignment = uiState.assignment!!
            val isChecklist = assignment.type == AssignmentType.CHECKLIST
            val typeColor = if (isChecklist) AccentPurple else DisciplineBlue
            val dateFormat = SimpleDateFormat("EEE, MMM d · h:mm a", Locale.getDefault())

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // Info Card
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(22.dp))
                            .background(BackgroundCard)
                            .border(1.dp, BorderCard, RoundedCornerShape(22.dp))
                            .padding(20.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(typeColor.copy(alpha = 0.15f))
                                        .border(1.dp, typeColor.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = if (isChecklist) "CHECKLIST QUEST" else "SIMPLE QUEST",
                                        color = typeColor,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }

                                Text(
                                    text = uiState.timeRemainingStr,
                                    color = if (uiState.liveStatus == AssignmentStatus.GRACE) Color(0xFFFF2D55) else AccentOrange,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = assignment.title,
                                color = TextPrimary,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )

                            if (assignment.description.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = assignment.description,
                                    color = TextSecondary,
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "⏰ Due: ${dateFormat.format(Date(assignment.dueAt))}",
                                color = TextMuted,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                // Completion Section
                item {
                    val completion = uiState.currentUserCompletion
                    if (completion != null) {
                        CompletionStatusCard(completion = completion)
                    } else if (isChecklist) {
                        ChecklistCompletionCard(
                            items = assignment.checklistItems,
                            checkedIds = uiState.checkedItemIds,
                            onToggleItem = viewModel::toggleChecklistItem,
                            isSubmitting = uiState.isSubmitting,
                            onSubmit = viewModel::submitCompletion
                        )
                    } else {
                        SimpleCompletionCard(
                            isSubmitting = uiState.isSubmitting,
                            onComplete = viewModel::submitCompletion
                        )
                    }
                }

                // Leaderboard Section
                item {
                    AssignmentLeaderboardList(entries = uiState.leaderboard)
                }

                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun AssignmentDetailScreenPreview() {
    QuestRingsTheme {
        AssignmentDetailScreen(onNavigateBack = {})
    }
}
