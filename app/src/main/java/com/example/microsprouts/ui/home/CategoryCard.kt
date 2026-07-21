package com.example.microsprouts.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
    // Left edge bar color (WarmSand hides bar for Uncategorized)
    val barColor: Color = if (category != null) {
        BrandPalette.getColorForIndex(categoryIndex)
    } else {
        WarmSand
    }

    val categoryTitle = category?.name ?: "Uncategorized"

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            // Left vertical color bar
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(barColor)
            )

            // Category Content Column
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Category Header Title
                Text(
                    text = categoryTitle,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateText,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // List of parent tasks inside this category
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    tasks.forEach { parentTask ->
                        key(parentTask.id) {
                            val dismissState = rememberSwipeToDismissBoxState(
                                confirmValueChange = { dismissValue ->
                                    // Validation only: return true if direction matches active tab
                                    when (dismissValue) {
                                        SwipeToDismissBoxValue.StartToEnd -> selectedTab == 0
                                        SwipeToDismissBoxValue.EndToStart -> selectedTab == 1
                                        else -> false
                                    }
                                }
                            )

                            // Handle state mutation safely outside the touch gesture pass
                            LaunchedEffect(dismissState.currentValue) {
                                when (dismissState.currentValue) {
                                    SwipeToDismissBoxValue.StartToEnd -> {
                                        if (selectedTab == 0) {
                                            onMoveToLater(parentTask)
                                        }
                                    }
                                    SwipeToDismissBoxValue.EndToStart -> {
                                        if (selectedTab == 1) {
                                            onMoveToToday(parentTask)
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
                                    val color = when (dismissState.dismissDirection) {
                                        SwipeToDismissBoxValue.StartToEnd -> Color(0xFFE6EFE9)
                                        SwipeToDismissBoxValue.EndToStart -> Color(0xFFEBF3FC)
                                        else -> Color.Transparent
                                    }
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(color, shape = RoundedCornerShape(16.dp))
                                            .padding(horizontal = 16.dp),
                                        contentAlignment = if (dismissState.dismissDirection == SwipeToDismissBoxValue.StartToEnd) {
                                            Alignment.CenterStart
                                        } else {
                                            Alignment.CenterEnd
                                        },
                                    ) {
                                        Text(
                                            text = if (dismissState.dismissDirection == SwipeToDismissBoxValue.StartToEnd) {
                                                "Move to Later ➔"
                                            } else {
                                                "⇠ Move to Today"
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
}