package com.example.microsprouts.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.microsprouts.data.entity.Category
import com.example.microsprouts.data.entity.Task
import com.example.microsprouts.data.entity.TaskCategoryCrossRef
import kotlinx.coroutines.flow.Flow
import androidx.room.Delete

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks")
    fun getAllTasksFlow(): Flow<List<Task>>

    @Query("SELECT * FROM tasks")
    suspend fun getAllTasksStatic(): List<Task>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long

    @Query("DELETE FROM tasks")
    suspend fun clearAllTasks()

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTaskById(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category)

    @Query("SELECT * FROM categories")
    suspend fun getAllCategories(): List<Category>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSecondaryCategory(crossRef: TaskCategoryCrossRef)

    @Query("DELETE FROM task_category_cross_ref WHERE taskId = :taskId")
    suspend fun deleteSecondaryCategoriesForTask(taskId: Long)

    @Query("SELECT * FROM categories WHERE id IN (SELECT categoryId FROM task_category_cross_ref WHERE taskId = :taskId)")
    suspend fun getSecondaryCategoriesForTask(taskId: Long): List<Category>

    @Query("SELECT * FROM tasks")
    fun getAllTasksRaw(): List<Task>

    @Delete
    suspend fun deleteTask(task: Task)
}
