package com.example.microsprouts.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.microsprouts.data.entity.Category
import com.example.microsprouts.data.entity.Task
import com.example.microsprouts.ui.theme.BrandPalette
import com.example.microsprouts.ui.theme.SlateText
import com.example.microsprouts.ui.theme.WarmSand

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryCard(
    category: Category?,
    categoryIndex: Int,
    tasks: List<Task>,
    selectedTab: Int,
    subtasksLookup: (Long) -> List<Task>,
    secondaryCategoriesLookup: (Long) -> List<Category>,
    allCategories: List<Category>,
    onToggle: (Task) -> Unit,
    onMoveToLater: (Task) -> Unit,
    onMoveToToday: (Task) -> Unit,
    onCardClick: (Task) -> Unit,
    onSubtaskClick: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    val barColor: Color = if (category != null) {
        BrandPalette.getColorForIndex(categoryIndex)
    } else {
        WarmSand
    }

    val categoryTitle = category?.name ?: "Uncategorized"

    Card(
        modifier = modifier
            .fillMaxWidth()
            .drawWithContent {
                drawContent()
                drawRect(
                    color = barColor,
                    topLeft = Offset.Zero,
                    size = Size(width = 6.dp.toPx(), height = size.height)
                )
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 22.dp, end = 16.dp, top = 16.dp, bottom = 16.dp)
        ) {
            Text(
                text = categoryTitle,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = SlateText,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                tasks.forEach { parentTask ->
                    key(parentTask.id, selectedTab) {
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = { dismissValue ->
                                when (dismissValue) {
                                    SwipeToDismissBoxValue.StartToEnd -> selectedTab == 0
                                    SwipeToDismissBoxValue.EndToStart -> selectedTab == 1
                                    else -> false
                                }
                            }
                        )

                        // Trigger state change immediately when target value changes
                        LaunchedEffect(dismissState.targetValue) {
                            when (dismissState.targetValue) {
                                SwipeToDismissBoxValue.StartToEnd -> {
                                    if (selectedTab == 0) {
                                        onMoveToLater(parentTask)
                                        dismissState.snapTo(SwipeToDismissBoxValue.Settled)
                                    }
                                }
                                SwipeToDismissBoxValue.EndToStart -> {
                                    if (selectedTab == 1) {
                                        onMoveToToday(parentTask)
                                        dismissState.snapTo(SwipeToDismissBoxValue.Settled)
                                    }
                                }
                                else -> {}
                            }
                        }

                        SwipeToDismissBox(
                            state = dismissState,
                            enableDismissFromStartToEnd = (selectedTab == 0),
                            enableDismissFromEndToStart = (selectedTab == 1),
                            backgroundContent = {
                                val direction = dismissState.targetValue
                                val color = when (direction) {
                                    SwipeToDismissBoxValue.StartToEnd -> Color(0xFFE6EFE9)
                                    SwipeToDismissBoxValue.EndToStart -> Color(0xFFEBF3FC)
                                    else -> Color.Transparent
                                }
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(color, shape = RoundedCornerShape(16.dp))
                                        .padding(horizontal = 16.dp),
                                    contentAlignment = if (direction == SwipeToDismissBoxValue.StartToEnd) {
                                        Alignment.CenterStart
                                    } else {
                                        Alignment.CenterEnd
                                    },
                                ) {
                                    Text(
                                        text = when (direction) {
                                            SwipeToDismissBoxValue.StartToEnd -> "Move to Later ➔"
                                            SwipeToDismissBoxValue.EndToStart -> "⇠ Move to Today"
                                            else -> ""
                                        },
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp,
                                    )
                                }
                            }
                        ) {
                            ParentTaskCard(
                                task = parentTask,
                                subtasks = subtasksLookup(parentTask.id),
                                allCategories = allCategories,
                                secondaryCategories = secondaryCategoriesLookup(parentTask.id),
                                onToggle = onToggle,
                                onCardClick = { onCardClick(parentTask) },
                                onSubtaskClick = onSubtaskClick,
                            )
                        }
                    }
                }
            }
        }
    }
}