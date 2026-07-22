package com.example.microsprouts.data.dao

import androidx.room.Dao
import androidx.room.Delete
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

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTaskById(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category)

    @Query("SELECT * FROM categories")
    suspend fun getAllCategories(): List<Category>

    @Query("SELECT * FROM categories")
    fun getAllCategoriesFlow(): Flow<List<Category>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSecondaryCategory(crossRef: TaskCategoryCrossRef)

    @Query("DELETE FROM task_category_cross_ref WHERE taskId = :taskId")
    suspend fun deleteSecondaryCategoriesForTask(taskId: Long)

    @Query("SELECT * FROM categories WHERE id IN (SELECT categoryId FROM task_category_cross_ref WHERE taskId = :taskId)")
    suspend fun getSecondaryCategoriesForTask(taskId: Long): List<Category>

    @Query("SELECT * FROM tasks")
    fun getAllTasksRaw(): List<Task>
    
    @Query("SELECT * FROM categories")
    fun getAllCategoriesRaw(): List<Category>
    
    @Query("SELECT * FROM task_category_cross_ref")
    fun getAllCrossRefsRaw(): List<TaskCategoryCrossRef>

    @Query("DELETE FROM task_category_cross_ref")
    suspend fun clearAllCrossRefs()

    @Query("DELETE FROM categories")
    suspend fun clearAllCategories()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaskRaw(task: Task)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategoryRaw(category: Category)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrossRefRaw(crossRef: TaskCategoryCrossRef)

    @Delete
    suspend fun deleteTask(task: Task)
}