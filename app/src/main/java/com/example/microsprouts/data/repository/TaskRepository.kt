package com.example.microsprouts.data.repository

import com.example.microsprouts.data.dao.TaskDao
import com.example.microsprouts.data.entity.Task
import kotlinx.coroutines.flow.Flow

class TaskRepository(
    private val taskDao: TaskDao,
) {

    val allTasks: Flow<List<Task>> = taskDao.getAllTasksFlow()

    suspend fun insertTask(task: Task) = taskDao.insertTask(task)

    suspend fun clearAllTasks() = taskDao.clearAllTasks()

    suspend fun seedSampleData() {
        clearAllTasks()
        insertTask(
            Task(
                id = 10L,
                title = "🌱 Run Morning Farm Inspection",
                isCompleted = false,
                recurrence = "Daily",
                parentId = null,
            ),
        )
        insertTask(
            Task(
                id = 11L,
                title = "Check moisture levels in tray room",
                isCompleted = false,
                parentId = 10L,
            ),
        )
        insertTask(
            Task(
                id = 12L,
                title = "Log ambient humidity readings",
                isCompleted = true,
                parentId = 10L,
            ),
        )
        insertTask(
            Task(
                id = 20L,
                title = "📦 Pack premium subscription boxes",
                isCompleted = false,
                parentId = null,
            ),
        )
        insertTask(
            Task(
                id = 30L,
                title = "🚜 Deep clean the sanitization bay",
                isCompleted = false,
                recurrence = "Custom:7",
                parentId = null,
            ),
        )
    }
}
