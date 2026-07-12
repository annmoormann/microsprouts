package com.example.microsprouts.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.microsprouts.data.entity.Task
import com.example.microsprouts.ui.taskdetail.TaskUiModel

@Composable
fun TaskTree(
    uiModel: TaskUiModel,
    onTaskClick: (Task) -> Unit,
    onToggleCompletion: (Task) -> Unit,
    onToggleExpansion: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onTaskClick(uiModel.task) }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = uiModel.task.isCompleted,
            onCheckedChange = { onToggleCompletion(uiModel.task) }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = uiModel.task.title,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
