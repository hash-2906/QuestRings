package com.questrings.core.designsystem.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.questrings.core.designsystem.theme.BorderSubtle
import com.questrings.core.designsystem.theme.BottomBarBackground
import com.questrings.core.designsystem.theme.DisciplineBlue
import com.questrings.core.designsystem.theme.QuestTheme
import com.questrings.core.designsystem.theme.TextMuted
import com.questrings.core.designsystem.theme.TextPrimary
import com.questrings.core.designsystem.theme.TextSecondary
import com.questrings.core.designsystem.theme.VitalityRed

import androidx.compose.material.icons.filled.Public

enum class QuestNavigationTab(
    val title: String,
    val icon: ImageVector
) {
    Rings("Rings", Icons.Default.AutoAwesome),
    Tasks("Tasks", Icons.Default.CheckCircle),
    Rooms("Rooms", Icons.Default.Groups),
    Social("Social", Icons.Default.Public),
    Profile("Profile", Icons.Default.Person)
}

@Composable
fun QuestBottomBar(
    currentTab: QuestNavigationTab,
    onTabSelected: (QuestNavigationTab) -> Unit,
    modifier: Modifier = Modifier,
    taskBadgeCount: Int = 0
) {
    val haptics = com.questrings.core.haptics.rememberQuestHaptics()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(BottomBarBackground)
            .navigationBarsPadding()
    ) {
        // Subtle top border
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(BorderSubtle)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            QuestNavigationTab.entries.forEach { tab ->
                val isSelected = tab == currentTab
                val animatedColor by animateColorAsState(
                    targetValue = if (isSelected) VitalityRed else TextSecondary,
                    label = "BottomBarColor"
                )

                val interactionSource = remember { MutableInteractionSource() }

                Box(
                    modifier = Modifier
                        .clickable(
                            interactionSource = interactionSource,
                            indication = ripple(bounded = false, radius = 24.dp),
                            onClick = {
                                haptics.lightImpact()
                                onTabSelected(tab)
                            }
                        )
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(contentAlignment = Alignment.TopEnd) {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title,
                                tint = animatedColor,
                                modifier = Modifier.size(22.dp)
                            )

                            if (tab == QuestNavigationTab.Tasks && taskBadgeCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .offset(x = 6.dp, y = (-4).dp)
                                        .size(14.dp)
                                        .clip(CircleShape)
                                        .background(DisciplineBlue),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$taskBadgeCount",
                                        style = QuestTheme.typography.monoKicker,
                                        color = TextPrimary,
                                        fontSize = 8.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = tab.title,
                            style = QuestTheme.typography.bodySmall,
                            color = if (isSelected) TextPrimary else TextMuted,
                            fontSize = 11.sp
                        )

                        // Glowing active dot
                        if (isSelected) {
                            Spacer(modifier = Modifier.height(3.dp))
                            Box(
                                modifier = Modifier
                                    .size(4.dp)
                                    .clip(CircleShape)
                                    .background(VitalityRed)
                            )
                        } else {
                            Spacer(modifier = Modifier.height(7.dp))
                        }
                    }
                }
            }
        }
    }
}
