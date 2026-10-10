package com.questrings.presentation.rooms.share.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.questrings.core.designsystem.theme.QuestTheme
import com.questrings.data.qr.QrCodeGenerator

@Composable
fun QrCodeDisplay(
    joinCode: String,
    size: Dp = 220.dp,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val qrGenerator = remember { QrCodeGenerator() }
    val deepLink = "questrings://join/$joinCode"
    val qrImage = remember(joinCode) { qrGenerator.generateImageBitmap(deepLink, 512) }

    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF111116))
            .border(1.5.dp, QuestTheme.colors.borderCard, RoundedCornerShape(24.dp))
            .clickable { onClick() }
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            bitmap = qrImage,
            contentDescription = "Room Join QR Code",
            contentScale = ContentScale.Fit,
            modifier = Modifier.clip(RoundedCornerShape(12.dp))
        )
    }
}
