package com.questrings.presentation.assignment.verification

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.questrings.core.designsystem.components.QuestButton
import com.questrings.core.designsystem.components.QuestButtonVariant
import com.questrings.core.designsystem.components.QuestTextField
import com.questrings.core.designsystem.theme.AccentGreen
import com.questrings.core.designsystem.theme.AccentPurple
import com.questrings.core.designsystem.theme.BackgroundCard
import com.questrings.core.designsystem.theme.BackgroundPrimary
import com.questrings.core.designsystem.theme.BorderCard
import com.questrings.core.designsystem.theme.QuestRingsTheme
import com.questrings.core.designsystem.theme.TextMuted
import com.questrings.core.designsystem.theme.TextPrimary
import com.questrings.core.designsystem.theme.TextSecondary
import com.questrings.domain.model.assignment.AssignmentCompletion
import com.questrings.domain.model.assignment.CompletionVerificationStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun VerificationQueueScreen(
    viewModel: VerificationQueueViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var rejectionTarget by remember { mutableStateOf<AssignmentCompletion?>(null) }
    var rejectionReason by remember { mutableStateOf("") }
    val dateFormat = SimpleDateFormat("MMM d · h:mm a", Locale.getDefault())

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
    ) {
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AccentPurple)
            }
        } else if (uiState.pendingCompletions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = AccentGreen,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "ALL CAUGHT UP!",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "No pending submissions awaiting moderator verification.",
                        color = TextMuted,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Text(
                        text = "PENDING VERIFICATIONS (${uiState.pendingCompletions.size})",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                }

                items(uiState.pendingCompletions, key = { it.id }) { comp ->
                    val avatarColor = runCatching {
                        Color(android.graphics.Color.parseColor(comp.avatarColorHex))
                    }.getOrDefault(AccentPurple)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(BackgroundCard)
                            .border(1.dp, BorderCard, RoundedCornerShape(18.dp))
                            .padding(16.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(avatarColor.copy(alpha = 0.25f))
                                        .border(1.5.dp, avatarColor, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = comp.displayName.firstOrNull()?.uppercase() ?: "A",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = comp.displayName,
                                        color = TextPrimary,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Submitted: ${dateFormat.format(Date(comp.completedAt))}",
                                        color = TextMuted,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }

                            if (comp.checkedItemIds.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Completed checklist items: ${comp.checkedItemIds.size}",
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                QuestButton(
                                    text = "REJECT",
                                    icon = Icons.Default.Close,
                                    variant = QuestButtonVariant.Secondary,
                                    modifier = Modifier.weight(1f),
                                    onClick = {
                                        rejectionTarget = comp
                                        rejectionReason = ""
                                    }
                                )
                                QuestButton(
                                    text = "APPROVE",
                                    icon = Icons.Default.Check,
                                    variant = QuestButtonVariant.Primary,
                                    modifier = Modifier.weight(1f),
                                    onClick = { viewModel.approve(comp) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Rejection Dialog
        rejectionTarget?.let { comp ->
            AlertDialog(
                onDismissRequest = { rejectionTarget = null },
                containerColor = BackgroundCard,
                title = {
                    Text(
                        text = "Reject Submission?",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column {
                        Text(
                            text = "Provide an optional reason so ${comp.displayName} knows what to fix:",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        QuestTextField(
                            value = rejectionReason,
                            onValueChange = { rejectionReason = it },
                            placeholder = "e.g., Code tests did not pass"
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.reject(comp.id, rejectionReason)
                            rejectionTarget = null
                        }
                    ) {
                        Text("REJECT", color = Color(0xFFFF2D55), fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { rejectionTarget = null }) {
                        Text("CANCEL", color = TextSecondary)
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun VerificationQueueScreenPreview() {
    QuestRingsTheme {
        VerificationQueueScreen()
    }
}
