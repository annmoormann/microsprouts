package com.example.microsprouts.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.microsprouts.data.entity.Category
import com.example.microsprouts.data.entity.Task
import com.example.microsprouts.data.entity.TaskCategoryCrossRef
import com.example.microsprouts.data.repository.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
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

    val allCategories: StateFlow<List<Category>> = flow {
        while (true) {
            emit(repository.getAllCategories())
            delay(3000)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList(),
    )

    val taskSecondaryCategories: StateFlow<Map<Long, List<Category>>> = repository.allTasks
        .map { tasks ->
            val map = mutableMapOf<Long, List<Category>>()
            tasks.forEach { task ->
                map[task.id] = repository.getSecondaryCategoriesForTask(task.id)
            }
            map
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

    fun insertCategory(name: String, colorHex: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertCategory(Category(name = name, colorHex = colorHex))
        }
    }

    fun insertTask(
        title: String,
        primaryCategoryId: Long?,
        secondaryCategoryIds: List<Long>,
        parentId: Long?
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val task = Task(
                title = title,
                primaryCategoryId = primaryCategoryId,
                parentId = parentId
            )
            val newTaskId = repository.insertTask(task)
            secondaryCategoryIds.forEach { catId ->
                repository.insertSecondaryCategory(
                    TaskCategoryCrossRef(taskId = newTaskId, categoryId = catId)
                )
            }
        }
    }

    fun updateTask(
        task: Task,
        secondaryCategoryIds: List<Long>
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertTask(task)
            repository.deleteSecondaryCategoriesForTask(task.id)
            secondaryCategoryIds.forEach { catId ->
                repository.insertSecondaryCategory(
                    TaskCategoryCrossRef(taskId = task.id, categoryId = catId)
                )
            }
        }
    }

    fun deleteTask(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTaskById(id)
            repository.deleteSecondaryCategoriesForTask(id)
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
