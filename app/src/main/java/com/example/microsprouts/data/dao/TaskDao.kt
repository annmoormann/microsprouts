package com.example.microsprouts.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.microsprouts.data.entity.Category
import com.example.microsprouts.data.entity.Task
import com.example.microsprouts.data.entity.TaskCategoryCrossRef
import kotlinx.coroutines.flow.Flow

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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category)

    @Query("SELECT * FROM categories")
    suspend fun getAllCategories(): List<Category>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSecondaryCategory(crossRef: TaskCategoryCrossRef)

    @Query("SELECT * FROM categories WHERE id IN (SELECT categoryId FROM task_category_cross_ref WHERE taskId = :taskId)")
    suspend fun getSecondaryCategoriesForTask(taskId: Long): List<Category>
}
