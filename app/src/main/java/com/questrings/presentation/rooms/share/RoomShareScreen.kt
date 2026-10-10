package com.questrings.presentation.rooms.share

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.questrings.core.designsystem.components.QuestButton
import com.questrings.core.designsystem.components.QuestButtonVariant
import com.questrings.core.designsystem.theme.QuestTheme
import com.questrings.presentation.rooms.share.components.QrCodeDisplay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomShareScreen(
    roomId: String,
    roomName: String = "Quest Syndicate",
    joinCode: String = "X7K2QR",
    onNavigateToRoom: (String) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isQrZoomed by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = QuestTheme.colors.backgroundPrimary,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "ROOM READY",
                        style = QuestTheme.typography.heroDisplay.copy(
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = QuestTheme.colors.textPrimary
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = QuestTheme.colors.textPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = QuestTheme.colors.backgroundPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Your room is forged!",
                    style = QuestTheme.typography.heroDisplay.copy(
                        fontSize = 24.sp,
                        color = QuestTheme.colors.textPrimary,
                        textAlign = TextAlign.Center
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Share the code or scan the QR code to gather your party.",
                    style = QuestTheme.typography.bodyMedium.copy(
                        color = QuestTheme.colors.textSecondary,
                        textAlign = TextAlign.Center
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // QR Code Card (Click to zoom)
                QrCodeDisplay(
                    joinCode = joinCode,
                    size = 230.dp,
                    onClick = { isQrZoomed = true }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Tap QR to zoom fullscreen",
                    style = QuestTheme.typography.monoLabel.copy(
                        fontSize = 10.sp,
                        color = QuestTheme.colors.textMuted
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Join Code Display Box
                Text(
                    text = "JOIN CODE",
                    style = QuestTheme.typography.monoLabel.copy(
                        fontSize = 11.sp,
                        color = QuestTheme.colors.textMuted,
                        letterSpacing = 1.sp
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(QuestTheme.colors.backgroundCard)
                        .border(1.dp, QuestTheme.colors.borderSubtle, RoundedCornerShape(16.dp))
                        .clickable {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Join Code", joinCode))
                            Toast.makeText(context, "Join code copied to clipboard!", Toast.LENGTH_SHORT).show()
                        }
                        .padding(horizontal = 20.dp, vertical = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = joinCode.map { "$it " }.joinToString("").trim(),
                            style = QuestTheme.typography.monoNumber.copy(
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = QuestTheme.colors.disciplineBlue,
                                letterSpacing = 4.sp
                            )
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy Code",
                                tint = QuestTheme.colors.textSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "COPY",
                                style = QuestTheme.typography.monoLabel.copy(
                                    fontSize = 11.sp,
                                    color = QuestTheme.colors.textSecondary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }

            // Bottom Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Share Intent
                QuestButton(
                    text = "📤 Share Code & Link",
                    onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "Join my QuestRings room '$roomName'!\n\nJoin Code: $joinCode\nLink: questrings://join/$joinCode"
                            )
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Quest Room"))
                    },
                    variant = QuestButtonVariant.Secondary,
                    modifier = Modifier.fillMaxWidth()
                )

                QuestButton(
                    text = "Enter Room →",
                    onClick = { onNavigateToRoom(roomId) },
                    variant = QuestButtonVariant.Primary,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    // Zoom Dialog
    if (isQrZoomed) {
        Dialog(onDismissRequest = { isQrZoomed = false }) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF111116))
                    .border(2.dp, QuestTheme.colors.disciplineBlue, RoundedCornerShape(24.dp))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    QrCodeDisplay(joinCode = joinCode, size = 300.dp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = joinCode,
                        style = QuestTheme.typography.monoNumber.copy(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }
        }
    }
}
