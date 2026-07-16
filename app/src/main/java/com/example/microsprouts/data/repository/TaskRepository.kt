package com.example.microsprouts.data.repository

import com.example.microsprouts.data.dao.TaskDao
import com.example.microsprouts.data.entity.Category
import com.example.microsprouts.data.entity.Task
import com.example.microsprouts.data.entity.TaskCategoryCrossRef
import com.example.microsprouts.data.entity.TaskList
import com.example.microsprouts.data.entity.RecurrenceBehavior
import kotlinx.coroutines.flow.Flow

class TaskRepository(
    private val taskDao: TaskDao,
) {

    val allTasks: Flow<List<Task>> = taskDao.getAllTasksFlow()

    // Helper for the background rollover engine to fetch all tasks synchronously
    suspend fun getAllTasksRaw(): List<Task> = taskDao.getAllTasksRaw()

    suspend fun insertTask(task: Task) = taskDao.insertTask(task)

    suspend fun clearAllTasks() = taskDao.clearAllTasks()

    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)

    suspend fun deleteTaskById(id: Long) = taskDao.deleteTaskById(id)

    suspend fun getAllCategories(): List<Category> = taskDao.getAllCategories()

    suspend fun insertCategory(category: Category) = taskDao.insertCategory(category)

    suspend fun insertSecondaryCategory(crossRef: TaskCategoryCrossRef) =
        taskDao.insertSecondaryCategory(crossRef)

    suspend fun deleteSecondaryCategoriesForTask(taskId: Long) =
        taskDao.deleteSecondaryCategoriesForTask(taskId)

    suspend fun getSecondaryCategoriesForTask(taskId: Long): List<Category> =
        taskDao.getSecondaryCategoriesForTask(taskId)

    suspend fun seedSampleData() {
        clearAllTasks()
        // Seed initial categories
        insertCategory(Category(id = 1L, name = "Watering", colorHex = "#4A90E2"))
        insertCategory(Category(id = 2L, name = "Harvesting", colorHex = "#E2844A"))
        insertCategory(Category(id = 3L, name = "Packaging", colorHex = "#50E3C2"))
        insertCategory(Category(id = 4L, name = "Cleaning", colorHex = "#B8E986"))

        // Morning Inspection: Set as Recurring, Daily (1 day), default to Today list
        insertTask(
            Task(
                id = 10L,
                title = "🌱 Run Morning Farm Inspection",
                isCompleted = false,
                currentList = TaskList.TODAY,
                isRecurring = true,
                intervalDays = 1,
                recurrenceBehavior = RecurrenceBehavior.SKIP,
                parentId = null,
                primaryCategoryId = 1L // Watering
            ),
        )
        // Seed secondary categories for parent task
        insertSecondaryCategory(TaskCategoryCrossRef(taskId = 10L, categoryId = 4L)) // Cleaning

        // Subtask 11
        insertTask(
            Task(
                id = 11L,
                title = "Check moisture levels in tray room",
                isCompleted = false,
                currentList = TaskList.TODAY,
                isRecurring = false,
                intervalDays = 0,
                recurrenceBehavior = RecurrenceBehavior.SKIP,
                parentId = 10L,
                primaryCategoryId = 1L,
            ),
        )

        // Subtask 12
        insertTask(
            Task(
                id = 12L,
                title = "Log ambient humidity readings",
                isCompleted = true,
                currentList = TaskList.TODAY,
                isRecurring = false,
                intervalDays = 0,
                recurrenceBehavior = RecurrenceBehavior.SKIP,
                parentId = 10L,
                primaryCategoryId = 4L,
            ),
        )

        // Packaging Task
        insertTask(
            Task(
                id = 20L,
                title = "📦 Pack premium subscription boxes",
                isCompleted = false,
                currentList = TaskList.TODAY,
                isRecurring = false,
                intervalDays = 0,
                recurrenceBehavior = RecurrenceBehavior.SKIP,
                parentId = null,
                primaryCategoryId = 3L, // Packaging
            ),
        )

        // Sanitization Bay: Set as Recurring, Weekly (7 days), default to Later list
        insertTask(
            Task(
                id = 30L,
                title = "🚜 Deep clean the sanitization bay",
                isCompleted = false,
                currentList = TaskList.LATER,
                isRecurring = true,
                intervalDays = 7,
                recurrenceBehavior = RecurrenceBehavior.SKIP,
                parentId = null,
                primaryCategoryId = 4L // Cleaning
            ),
        )
    }
}