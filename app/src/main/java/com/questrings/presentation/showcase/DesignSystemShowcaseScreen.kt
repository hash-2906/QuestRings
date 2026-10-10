package com.questrings.presentation.showcase

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Sparkles
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.questrings.core.designsystem.components.CategoryTab
import com.questrings.core.designsystem.components.ConcentricRings
import com.questrings.core.designsystem.components.QuestButton
import com.questrings.core.designsystem.components.QuestButtonVariant
import com.questrings.core.designsystem.components.QuestCard
import com.questrings.core.designsystem.components.QuestChip
import com.questrings.core.designsystem.components.RingProgressData
import com.questrings.core.designsystem.components.StreakBadge
import com.questrings.core.designsystem.components.XpProgressBar
import com.questrings.core.designsystem.theme.BackgroundCard
import com.questrings.core.designsystem.theme.BackgroundPrimary
import com.questrings.core.designsystem.theme.BackgroundSecondary
import com.questrings.core.designsystem.theme.BackgroundTrack
import com.questrings.core.designsystem.theme.BorderCard
import com.questrings.core.designsystem.theme.BorderSubtle
import com.questrings.core.designsystem.theme.DisciplineBlue
import com.questrings.core.designsystem.theme.HeroTextGradient
import com.questrings.core.designsystem.theme.IntellectGreen
import com.questrings.core.designsystem.theme.QuestTheme
import com.questrings.core.designsystem.theme.RoyalPurple
import com.questrings.core.designsystem.theme.StreakOrange
import com.questrings.core.designsystem.theme.TextMuted
import com.questrings.core.designsystem.theme.TextPrimary
import com.questrings.core.designsystem.theme.TextSecondary
import com.questrings.core.designsystem.theme.VitalityRed

@Composable
fun DesignSystemShowcaseScreen(
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    // Interactive showcase states
    var vitalityProgress by remember { mutableFloatStateOf(0.75f) }
    var intellectProgress by remember { mutableFloatStateOf(0.50f) }
    var disciplineProgress by remember { mutableFloatStateOf(0.90f) }
    var xpValue by remember { mutableIntStateOf(650) }
    var selectedCategoryIndex by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        // Showcase Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                QuestChip(
                    text = "DESIGN SYSTEM v1.0",
                    textColor = RoyalPurple,
                    backgroundColor = RoyalPurple.copy(alpha = 0.15f),
                    borderColor = RoyalPurple.copy(alpha = 0.35f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Component Gallery",
                    style = QuestTheme.typography.headlineLarge,
                    color = TextPrimary
                )
                Text(
                    text = "Dark Cyberpunk / RPG Design System",
                    style = QuestTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }

            QuestButton(
                text = "Launch App",
                onClick = onNavigateToHome,
                variant = QuestButtonVariant.Primary,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = TextPrimary
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // SECTION 1: Color Palette Tokens
        ShowcaseSection(title = "1. COLOR PALETTE TOKENS") {
            Text(
                text = "Neutrals & Cyberpunk Surface Tokens",
                style = QuestTheme.typography.bodySmall,
                color = TextMuted
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ColorSwatch(color = BackgroundPrimary, name = "Primary", hex = "#000000", modifier = Modifier.weight(1f))
                ColorSwatch(color = BackgroundSecondary, name = "Secondary", hex = "#111116", modifier = Modifier.weight(1f))
                ColorSwatch(color = BackgroundCard, name = "Card", hex = "#17171D", modifier = Modifier.weight(1f))
                ColorSwatch(color = BorderCard, name = "Border", hex = "#29292F", modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Ring Category & RPG Accent Accents",
                style = QuestTheme.typography.bodySmall,
                color = TextMuted
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ColorSwatch(color = VitalityRed, name = "Vitality", hex = "#FF2D55", modifier = Modifier.weight(1f))
                ColorSwatch(color = IntellectGreen, name = "Intellect", hex = "#30D158", modifier = Modifier.weight(1f))
                ColorSwatch(color = DisciplineBlue, name = "Discipline", hex = "#0A84FF", modifier = Modifier.weight(1f))
                ColorSwatch(color = StreakOrange, name = "Streak", hex = "#FF8B35", modifier = Modifier.weight(1f))
                ColorSwatch(color = RoyalPurple, name = "Level XP", hex = "#7E5DFF", modifier = Modifier.weight(1f))
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // SECTION 2: Typography
        ShowcaseSection(title = "2. TYPOGRAPHY SYSTEM") {
            QuestCard(
                modifier = Modifier.fillMaxWidth(),
                glossyHighlight = true
            ) {
                Column {
                    Text(
                        text = "HERO DISPLAY WITH PLAYFAIR ITALIC",
                        style = QuestTheme.typography.monoKicker
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = buildAnnotatedString {
                            append("Make today\n")
                            withStyle(
                                SpanStyle(
                                    brush = HeroTextGradient,
                                    fontFamily = QuestTheme.typography.heroAccent.fontFamily,
                                    fontStyle = QuestTheme.typography.heroAccent.fontStyle,
                                    fontWeight = QuestTheme.typography.heroAccent.fontWeight,
                                    fontSize = QuestTheme.typography.heroAccent.fontSize
                                )
                            ) {
                                append("legendary.")
                            }
                        },
                        style = QuestTheme.typography.heroTitle
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "DM Sans: Body, buttons, headlines & labels",
                        style = QuestTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "DM Mono: 01:23:45 • 540/1,000 XP • LVL 7 GUARDIAN • +25 XP",
                        style = QuestTheme.typography.monoCounter,
                        color = IntellectGreen
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // SECTION 3: Concentric Rings Canvas Component
        ShowcaseSection(title = "3. CONCENTRIC RINGS (CANVAS COMPONENT)") {
            QuestCard(
                modifier = Modifier.fillMaxWidth(),
                radialGlowColor = DisciplineBlue
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val ringData = RingProgressData(
                        vitalityProgress = vitalityProgress,
                        intellectProgress = intellectProgress,
                        disciplineProgress = disciplineProgress
                    )

                    ConcentricRings(
                        data = ringData,
                        size = 250.dp,
                        strokeWidth = 16.dp
                    )

                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "Live Interactive Controls",
                        style = QuestTheme.typography.titleSmall,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Vitality Slider
                    RingSliderRow(
                        label = "Vitality",
                        value = vitalityProgress,
                        color = VitalityRed,
                        onValueChange = { vitalityProgress = it }
                    )

                    // Intellect Slider
                    RingSliderRow(
                        label = "Intellect",
                        value = intellectProgress,
                        color = IntellectGreen,
                        onValueChange = { intellectProgress = it }
                    )

                    // Discipline Slider
                    RingSliderRow(
                        label = "Discipline",
                        value = disciplineProgress,
                        color = DisciplineBlue,
                        onValueChange = { disciplineProgress = it }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // SECTION 4: Buttons
        ShowcaseSection(title = "4. BUTTON COMPONENT (QUESTBUTTON)") {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                QuestButton(
                    text = "Primary Gradient Button",
                    onClick = { /* Demo */ },
                    variant = QuestButtonVariant.Primary,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextPrimary)
                    }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuestButton(
                        text = "Secondary Button",
                        onClick = { /* Demo */ },
                        variant = QuestButtonVariant.Secondary,
                        modifier = Modifier.weight(1f)
                    )

                    QuestButton(
                        text = "Ghost Button",
                        onClick = { /* Demo */ },
                        variant = QuestButtonVariant.Ghost,
                        modifier = Modifier.weight(1f)
                    )
                }

                QuestButton(
                    text = "Disabled State",
                    onClick = { /* Demo */ },
                    variant = QuestButtonVariant.Primary,
                    enabled = false,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // SECTION 5: XP Progress Bar
        ShowcaseSection(title = "5. XP PROGRESS BAR") {
            QuestCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    XpProgressBar(
                        currentXp = xpValue,
                        targetXp = 1000
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        QuestButton(
                            text = "+100 XP",
                            onClick = { xpValue = (xpValue + 100).coerceAtMost(1000) },
                            variant = QuestButtonVariant.Secondary,
                            modifier = Modifier.weight(1f)
                        )
                        QuestButton(
                            text = "Reset XP",
                            onClick = { xpValue = 150 },
                            variant = QuestButtonVariant.Secondary,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // SECTION 6: Streak Badge
        ShowcaseSection(title = "6. STREAK BADGE") {
            QuestCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StreakBadge(
                        streakDays = 12,
                        subtitle = "Keep the fire alive"
                    )

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "COMPACT VARIANT",
                            style = QuestTheme.typography.monoKicker
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        StreakBadge(
                            streakDays = 12,
                            isCompact = true
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // SECTION 7: Category Tabs & Quest Chips
        ShowcaseSection(title = "7. CATEGORY TABS & QUEST CHIPS") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CategoryTab(
                    title = "Vitality",
                    completedCount = 2,
                    totalCount = 2,
                    categoryColor = VitalityRed,
                    isSelected = selectedCategoryIndex == 0,
                    onSelect = { selectedCategoryIndex = 0 },
                    icon = Icons.Default.FitnessCenter,
                    modifier = Modifier.weight(1f)
                )

                CategoryTab(
                    title = "Intellect",
                    completedCount = 1,
                    totalCount = 2,
                    categoryColor = IntellectGreen,
                    isSelected = selectedCategoryIndex == 1,
                    onSelect = { selectedCategoryIndex = 1 },
                    icon = Icons.Default.Psychology,
                    modifier = Modifier.weight(1f)
                )

                CategoryTab(
                    title = "Discipline",
                    completedCount = 0,
                    totalCount = 2,
                    categoryColor = DisciplineBlue,
                    isSelected = selectedCategoryIndex == 2,
                    onSelect = { selectedCategoryIndex = 2 },
                    icon = Icons.Default.WaterDrop,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuestChip(
                    text = "DAILY QUEST",
                    icon = Icons.Default.Sparkles,
                    textColor = Color(0xFFC4C4CE)
                )

                QuestChip(
                    text = "+25 XP",
                    textColor = IntellectGreen,
                    backgroundColor = IntellectGreen.copy(alpha = 0.12f),
                    borderColor = IntellectGreen.copy(alpha = 0.3f)
                )

                QuestChip(
                    text = "LVL 7 GUARDIAN",
                    textColor = RoyalPurple,
                    backgroundColor = RoyalPurple.copy(alpha = 0.12f),
                    borderColor = RoyalPurple.copy(alpha = 0.3f)
                )
            }
        }

        Spacer(modifier = Modifier.height(36.dp))
    }
}

@Composable
private fun ShowcaseSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = QuestTheme.typography.monoKicker,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(12.dp))
        content()
    }
}

@Composable
private fun ColorSwatch(
    color: Color,
    name: String,
    hex: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(BackgroundSecondary)
            .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(color)
                .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = name,
            style = QuestTheme.typography.bodySmall,
            color = TextPrimary,
            fontSize = 10.sp,
            maxLines = 1
        )
        Text(
            text = hex,
            style = QuestTheme.typography.monoKicker,
            color = TextMuted,
            fontSize = 9.sp
        )
    }
}

@Composable
private fun RingSliderRow(
    label: String,
    value: Float,
    color: Color,
    onValueChange: (Float) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "$label: ${(value * 100).toInt()}%",
            style = QuestTheme.typography.monoCounter,
            color = color,
            modifier = Modifier.width(105.dp),
            fontSize = 11.sp
        )
        Slider(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
            colors = SliderDefaults.colors(
                thumbColor = color,
                activeTrackColor = color,
                inactiveTrackColor = BackgroundTrack
            )
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Design System Showcase",
    showBackground = true,
    backgroundColor = 0xFF000000
)
@Composable
private fun DesignSystemShowcasePreview() {
    com.questrings.core.designsystem.theme.QuestRingsTheme {
        DesignSystemShowcaseScreen(
            onNavigateToHome = {}
        )
    }
}

