package com.example.microsprouts.ui.taskdetail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.microsprouts.data.entity.Task
import com.example.microsprouts.ui.common.LocalTaskRepository
import com.example.microsprouts.ui.common.TaskViewModelFactory
import com.example.microsprouts.ui.home.TaskTree


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    taskId: Long,
    onNavigateBack: () -> Unit,
    onEditTask: (Long) -> Unit,
    onAddSubtask: (Long) -> Unit
) {
    val repository = LocalTaskRepository.current
    val viewModel: TaskDetailViewModel = viewModel(
        key = "TaskDetail_$taskId",
        factory = TaskViewModelFactory(repository = repository, taskId = taskId)
    )
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Task Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { onEditTask(taskId) }) {
                        Icon(Icons.Rounded.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = { 
                        viewModel.deleteTask()
                        onNavigateBack()
                    }) {
                        Icon(Icons.Rounded.Delete, contentDescription = "Delete")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { onAddSubtask(taskId) },
                icon = { Icon(Icons.Rounded.Add, contentDescription = null) },
                text = { Text("Add Subtask") },
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
            )
        }
    ) { innerPadding ->
        val task = uiState.task
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (task == null) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text("Task not found")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Title and Status
                item {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = task.isCompleted,
                                onCheckedChange = { viewModel.toggleTaskCompletion(task) }
                            )
                            Text(
                                text = task.title,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        if (task.description.isNotBlank()) {
                            Text(
                                text = task.description,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(start = 48.dp, top = 8.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Scheduling & Recurrence
                item {
                    DetailSection(title = "Scheduling", icon = Icons.Rounded.Schedule) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            SuggestionChip(
                                onClick = { },
                                label = { Text("Starts at ${task.startTimeOfDay}") },
                                icon = { Icon(Icons.Rounded.AccessTime, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            )
                            
                            if (task.recurrence != null) {
                                val displayRec = if (task.recurrence.startsWith("Custom:")) {
                                    "Every ${task.recurrence.substringAfter(":")} days"
                                } else {
                                    task.recurrence
                                }
                                SuggestionChip(
                                    onClick = { },
                                    label = { Text(displayRec) },
                                    icon = { Icon(Icons.Rounded.Repeat, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                )
                            }
                            
                            SuggestionChip(
                                onClick = { },
                                label = { Text("Missed: ${task.missedBehavior.name.replace("_", " ").lowercase().capitalize()}") },
                                icon = { Icon(Icons.Rounded.Warning, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            )
                        }
                    }
                }

                // Blockers
                if (uiState.blockers.isNotEmpty()) {
                    item {
                        DetailSection(title = "Blockers", icon = Icons.Rounded.Block) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                uiState.blockers.forEach { blocker ->
                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (blocker.isCompleted) 
                                                MaterialTheme.colorScheme.surfaceVariant 
                                            else 
                                                MaterialTheme.colorScheme.errorContainer
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = if (blocker.isCompleted) Icons.Rounded.CheckCircle else Icons.Rounded.Error,
                                                contentDescription = null,
                                                tint = if (blocker.isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                                            )
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Text(
                                                text = blocker.title, 
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = if (blocker.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onErrorContainer
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Subtasks
                item {
                    DetailSection(title = "Subtasks", icon = Icons.Rounded.AccountTree) {
                        if (uiState.subtasks.isEmpty()) {
                            Text("No subtasks yet", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        } else {
                            Column {
                                uiState.subtasks.forEach { subtask ->
                                    TaskTree(
                                        uiModel = subtask,
                                        onTaskClick = { /* No-op in this view */ },
                                        onToggleCompletion = { viewModel.toggleTaskCompletion(it) },
                                        onToggleExpansion = { /* detail subtasks always visible */ }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailSection(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
        content()
    }
}

private fun String.capitalize() = this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
