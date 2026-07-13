package com.example.microsprouts.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.microsprouts.data.entity.Task
import com.example.microsprouts.data.repository.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: TaskRepository,
) : ViewModel() {

    val todayTasks: StateFlow<List<Task>> = repository.allTasks
        .map { tasks ->
            tasks.filter { task ->
                val isParent = task.parentId == null
                val isDailyOrOnce = (task.recurrence == "Daily") || task.recurrence.isNullOrEmpty()
                isParent && isDailyOrOnce
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList(),
        )

    val laterTasks: StateFlow<List<Task>> = repository.allTasks
        .map { tasks ->
            tasks.filter { task ->
                val isParent = task.parentId == null
                val isLater = !task.recurrence.isNullOrEmpty() && (task.recurrence != "Daily")
                isParent && isLater
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList(),
        )

    val subtasks: StateFlow<Map<Long, List<Task>>> = repository.allTasks
        .map { tasks ->
            tasks.asSequence()
                .filter { it.parentId != null }
                .groupBy { it.parentId!! }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap(),
        )

    fun insertTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertTask(task)
        }
    }

    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertTask(task.copy(isCompleted = !task.isCompleted))
        }
    }

    fun seedSampleData() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.seedSampleData()
        }
    }

    fun clearAllTasks() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearAllTasks()
        }
    }
}
