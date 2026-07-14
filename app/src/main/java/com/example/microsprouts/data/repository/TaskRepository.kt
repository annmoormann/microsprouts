package com.example.microsprouts.data.repository

import com.example.microsprouts.data.dao.TaskDao
import com.example.microsprouts.data.entity.Category
import com.example.microsprouts.data.entity.Task
import com.example.microsprouts.data.entity.TaskCategoryCrossRef
import kotlinx.coroutines.flow.Flow

class TaskRepository(
    private val taskDao: TaskDao,
) {

    val allTasks: Flow<List<Task>> = taskDao.getAllTasksFlow()

    suspend fun insertTask(task: Task) = taskDao.insertTask(task)

    suspend fun clearAllTasks() = taskDao.clearAllTasks()

    suspend fun getAllCategories(): List<Category> = taskDao.getAllCategories()

    suspend fun insertCategory(category: Category) = taskDao.insertCategory(category)

    suspend fun insertSecondaryCategory(crossRef: TaskCategoryCrossRef) =
        taskDao.insertSecondaryCategory(crossRef)

    suspend fun getSecondaryCategoriesForTask(taskId: Long): List<Category> =
        taskDao.getSecondaryCategoriesForTask(taskId)

    suspend fun seedSampleData() {
        clearAllTasks()
        // Seed initial categories
        insertCategory(Category(id = 1L, name = "Watering", colorHex = "#4A90E2"))
        insertCategory(Category(id = 2L, name = "Harvesting", colorHex = "#E2844A"))
        insertCategory(Category(id = 3L, name = "Packaging", colorHex = "#50E3C2"))
        insertCategory(Category(id = 4L, name = "Cleaning", colorHex = "#B8E986"))

        insertTask(
            Task(
                id = 10L,
                title = "🌱 Run Morning Farm Inspection",
                isCompleted = false,
                recurrence = "Daily",
                parentId = null,
                primaryCategoryId = 1L // Watering
            ),
        )
        // Seed secondary categories for parent task
        insertSecondaryCategory(TaskCategoryCrossRef(taskId = 10L, categoryId = 4L)) // Cleaning

        insertTask(
            Task(
                id = 11L,
                title = "Check moisture levels in tray room",
                isCompleted = false,
                parentId = 10L,
                primaryCategoryId = 1L
            ),
        )
        insertTask(
            Task(
                id = 12L,
                title = "Log ambient humidity readings",
                isCompleted = true,
                parentId = 10L,
                primaryCategoryId = 4L
            ),
        )
        insertTask(
            Task(
                id = 20L,
                title = "📦 Pack premium subscription boxes",
                isCompleted = false,
                parentId = null,
                primaryCategoryId = 3L // Packaging
            ),
        )
        insertTask(
            Task(
                id = 30L,
                title = "🚜 Deep clean the sanitization bay",
                isCompleted = false,
                recurrence = "Custom:7",
                parentId = null,
                primaryCategoryId = 4L // Cleaning
            ),
        )
    }
}
