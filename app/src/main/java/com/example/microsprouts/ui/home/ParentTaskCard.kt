package com.example.microsprouts.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.microsprouts.data.entity.Category
import com.example.microsprouts.data.entity.Task
import com.example.microsprouts.ui.components.CategoryChip

@Composable
fun ParentTaskCard(
    task: Task,
    subtasks: List<Task>,
    allCategories: List<Category>,
    secondaryCategories: List<Category>,
    onToggle: (Task) -> Unit,
    onCardClick: () -> Unit,
    onSubtaskClick: (Task) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCardClick() },
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
                .padding(16.dp),
        ) {
            val isCompleted = task.isCompleted
            val textAlpha = if (isCompleted) 0.5f else 1f
            val textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    checked = task.isCompleted,
                    onCheckedChange = { onToggle(task) },
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary,
                    ),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .alpha(textAlpha),
                ) {
                    Text(
                        text = task.title,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        textDecoration = textDecoration,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (task.description.isNotEmpty()) {
                        Text(
                            text = task.description,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }

                    // Category Chips
                    val primaryCategory = allCategories.find { it.id == task.primaryCategoryId }
                    if (primaryCategory != null || secondaryCategories.isNotEmpty()) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 6.dp, bottom = 2.dp)
                        ) {
                            primaryCategory?.let { cat ->
                                val catIndex = allCategories.indexOf(cat).coerceAtLeast(0)
                                CategoryChip(category = cat, categoryIndex = catIndex)
                            }
                            secondaryCategories.forEach { cat ->
                                val catIndex = allCategories.indexOf(cat).coerceAtLeast(0)
                                CategoryChip(category = cat, categoryIndex = catIndex)
                            }
                        }
                    }

                    if (task.isRecurring) {
                        Text(
                            text = "${getRecurrenceDisplayText(task)} (${task.recurrenceBehavior.name})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(top = 2.dp),
                        )
                    }
                }
            }

            if (subtasks.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    subtasks.forEach { subtask ->
                        SubtaskItemRow(
                            subtask = subtask,
                            onToggle = onToggle,
                            onSubtaskClick = onSubtaskClick
                        )
                    }
                }
            }
        }
    }
}