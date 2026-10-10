package com.questrings.presentation.rooms.join.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.questrings.core.designsystem.theme.AccentPurple
import com.questrings.core.designsystem.theme.BackgroundCard
import com.questrings.core.designsystem.theme.BorderCard
import com.questrings.core.designsystem.theme.BorderSubtle
import com.questrings.core.designsystem.theme.QuestRingsTheme
import com.questrings.core.designsystem.theme.TextMuted
import com.questrings.core.designsystem.theme.TextPrimary

@Composable
fun OtpCodeInput(
    code: String,
    onCodeChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    codeLength: Int = 6,
    onComplete: (String) -> Unit = {}
) {
    val focusRequester = remember { FocusRequester() }
    val validChars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"

    // Invisible single textfield underneath that captures input & paste reliably across all IME keyboards
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        BasicTextField(
            value = code,
            onValueChange = { newValue ->
                val filtered = newValue
                    .uppercase()
                    .filter { it in validChars }
                    .take(codeLength)
                onCodeChange(filtered)
                if (filtered.length == codeLength) {
                    onComplete(filtered)
                }
            },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Characters,
                keyboardType = KeyboardType.Ascii,
                imeAction = if (code.length == codeLength) ImeAction.Done else ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (code.length == codeLength) {
                        onComplete(code)
                    }
                }
            ),
            cursorBrush = SolidColor(Color.Transparent),
            modifier = Modifier
                .focusRequester(focusRequester)
                .size(1.dp),
            decorationBox = { /* Hidden, styled boxes rendered below */ }
        )

        // Visual 6-box row
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (index in 0 until codeLength) {
                val char = code.getOrNull(index)
                val isFocused = code.length == index || (code.length == codeLength && index == codeLength - 1)
                val isFilled = char != null

                val borderColor by animateColorAsState(
                    targetValue = when {
                        isFocused -> AccentPurple
                        isFilled -> BorderCard
                        else -> BorderSubtle
                    },
                    label = "border_color_$index"
                )

                Box(
                    modifier = Modifier
                        .size(width = 48.dp, height = 58.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(BackgroundCard)
                        .border(
                            width = if (isFocused) 1.5.dp else 1.dp,
                            color = borderColor,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            focusRequester.requestFocus()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = char?.toString() ?: "",
                        color = TextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        textAlign = TextAlign.Center
                    )

                    // Blinking or subtle underline marker if active and empty
                    if (isFocused && !isFilled) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth(0.5f)
                                .height(2.dp)
                                .background(AccentPurple)
                        )
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun OtpCodeInputPreview() {
    QuestRingsTheme {
        OtpCodeInput(
            code = "X7K2",
            onCodeChange = {}
        )
    }
}
