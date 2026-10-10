package com.questrings.core.designsystem.components.skeletons

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.questrings.core.designsystem.components.shimmerPlaceholder
import com.questrings.core.designsystem.theme.BorderDark
import com.questrings.core.designsystem.theme.DarkBackground
import com.questrings.core.designsystem.theme.QuestRingsTheme
import com.questrings.core.designsystem.theme.SurfaceDark

/**
 * Skeleton placeholder for Habit Cards on the Rings home screen.
 */
@Composable
fun HabitCardSkeleton(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(SurfaceDark, RoundedCornerShape(16.dp))
            .border(1.dp, BorderDark, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category icon placeholder
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .shimmerPlaceholder(shape = RoundedCornerShape(12.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Title line
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.65f)
                        .height(14.dp)
                        .shimmerPlaceholder(shape = RoundedCornerShape(4.dp))
                )
                // Goal subtitle line
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .height(10.dp)
                        .shimmerPlaceholder(shape = RoundedCornerShape(4.dp))
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Completion check placeholder
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .shimmerPlaceholder(shape = CircleShape)
            )
        }
    }
}

/**
 * Skeleton placeholder for Task Cards on the Tasks scheduler screen.
 */
@Composable
fun TaskCardSkeleton(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(SurfaceDark, RoundedCornerShape(16.dp))
            .border(1.dp, BorderDark, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Priority badge placeholder
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(18.dp)
                        .shimmerPlaceholder(shape = RoundedCornerShape(6.dp))
                )
                // XP pill placeholder
                Box(
                    modifier = Modifier
                        .width(55.dp)
                        .height(18.dp)
                        .shimmerPlaceholder(shape = RoundedCornerShape(6.dp))
                )
            }

            // Task title
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(16.dp)
                    .shimmerPlaceholder(shape = RoundedCornerShape(4.dp))
            )

            // Countdown timer line
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(12.dp)
                    .shimmerPlaceholder(shape = RoundedCornerShape(4.dp))
            )
        }
    }
}

/**
 * Skeleton placeholder for Room Cards on the Chambers / Rooms screen.
 */
@Composable
fun RoomCardSkeleton(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(SurfaceDark, RoundedCornerShape(16.dp))
            .border(1.dp, BorderDark, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Room avatar emoji box
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .shimmerPlaceholder(shape = RoundedCornerShape(14.dp))
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Room name
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(16.dp)
                        .shimmerPlaceholder(shape = RoundedCornerShape(4.dp))
                )
                // Description line
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(11.dp)
                        .shimmerPlaceholder(shape = RoundedCornerShape(4.dp))
                )
                // Members count pill
                Box(
                    modifier = Modifier
                        .width(70.dp)
                        .height(12.dp)
                        .shimmerPlaceholder(shape = RoundedCornerShape(4.dp))
                )
            }
        }
    }
}

/**
 * Skeleton placeholder for Social Activity Feed cards.
 */
@Composable
fun FeedCardSkeleton(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(SurfaceDark, RoundedCornerShape(16.dp))
            .border(1.dp, BorderDark, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // User avatar circle
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .shimmerPlaceholder(shape = CircleShape)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(
                        modifier = Modifier
                            .width(100.dp)
                            .height(14.dp)
                            .shimmerPlaceholder(shape = RoundedCornerShape(4.dp))
                    )
                    Box(
                        modifier = Modifier
                            .width(60.dp)
                            .height(10.dp)
                            .shimmerPlaceholder(shape = RoundedCornerShape(4.dp))
                    )
                }
            }

            // Milestone description box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .shimmerPlaceholder(shape = RoundedCornerShape(10.dp))
            )

            // Reactions row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(3) {
                    Box(
                        modifier = Modifier
                            .width(48.dp)
                            .height(28.dp)
                            .shimmerPlaceholder(shape = RoundedCornerShape(8.dp))
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun SkeletonsPreview() {
    QuestRingsTheme {
        Column(
            modifier = Modifier
                .background(DarkBackground)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            HabitCardSkeleton()
            TaskCardSkeleton()
            RoomCardSkeleton()
            FeedCardSkeleton()
        }
    }
}
