package com.example.microsprouts.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.microsprouts.data.entity.Task

@Composable
fun SubtaskItemRow(
    subtask: Task,
    onToggle: (Task) -> Unit,
    onSubtaskClick: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    val subCompleted = subtask.isCompleted
    val subTextAlpha = if (subCompleted) 0.5f else 1f
    val subTextDecoration = if (subCompleted) TextDecoration.LineThrough else TextDecoration.None

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onSubtaskClick(subtask) }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = subtask.isCompleted,
            onCheckedChange = { onToggle(subtask) },
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.primary,
            ),
            modifier = Modifier.size(24.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = subtask.title,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface,
            textDecoration = subTextDecoration,
            modifier = Modifier
                .weight(1f)
                .alpha(subTextAlpha),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}