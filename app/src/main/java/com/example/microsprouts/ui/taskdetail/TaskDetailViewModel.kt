package com.example.microsprouts.ui.taskdetail

import androidx.lifecycle.ViewModel
import com.example.microsprouts.data.entity.Task
import com.example.microsprouts.data.repository.TaskRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

// Helper UI representation model utilized by TaskTree
data class TaskUiModel(
    val task: Task,
    val isExpanded: Boolean = true,
    val children: List<TaskUiModel> = emptyList()
)

data class TaskDetailUiState(
    val isLoading: Boolean = false,
    val task: Task? = null,
    val blockers: List<Task> = emptyList(),
    val subtasks: List<TaskUiModel> = emptyList()
)

@Suppress("UNUSED_PARAMETER")
class TaskDetailViewModel(
    private val repository: TaskRepository,
    private val taskId: Long
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskDetailUiState(task = Task(id = taskId, title = "Loading Task Data...")))
    val uiState: StateFlow<TaskDetailUiState> = _uiState.asStateFlow()

    fun deleteTask() {
        // Stub implementation
    }

    fun toggleTaskCompletion(task: Task) {
        // Stub implementation
    }
}