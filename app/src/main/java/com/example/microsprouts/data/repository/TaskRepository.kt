package com.example.microsprouts.data.repository

import com.example.microsprouts.data.dao.TaskDao
import com.example.microsprouts.data.entity.Task
import com.example.microsprouts.data.entity.MissedBehavior
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {

    fun getAllTasks(): Flow<List<Task>> {
        return taskDao.getAllTasks()
    }

    suspend fun insertTask(task: Task) {
        taskDao.insertTask(task)
    }

    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task)
    }

    suspend fun updateTask(task: Task) {
        taskDao.insertTask(task)
    }

    suspend fun seedSampleData() {
        val sampleTasks = listOf(
            Task(
                id = 1L,
                title = "🌱 Water Sunflower Sprouts",
                description = "Give them 100ml of water",
                isCompleted = false,
                startTimeOfDay = "08:00",
                missedBehavior = MissedBehavior.SKIP,
                parentId = null
            ),
            Task(
                id = 2L,
                title = "Check soil moisture",
                description = "Check if it's damp enough",
                isCompleted = false,
                startTimeOfDay = "08:05",
                missedBehavior = MissedBehavior.SKIP,
                parentId = 1L
            ),
            Task(
                id = 3L,
                title = "Harvest Wheatgrass",
                description = "Cut and prep for juicing",
                isCompleted = false,
                startTimeOfDay = "18:00",
                missedBehavior = MissedBehavior.REPLACE,
                parentId = null
            ),
            Task(
                id = 4L,
                title = "Clean propagation trays",
                description = "Sanitize with soap and water",
                isCompleted = false,
                startTimeOfDay = "19:00",
                missedBehavior = MissedBehavior.ADD_NEW,
                parentId = null
            )
        )
        taskDao.insertTasks(sampleTasks)
    }
}
