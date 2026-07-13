package com.example.microsprouts

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.microsprouts.data.dao.TaskDao
import com.example.microsprouts.data.database.MicroSproutsDatabase
import com.example.microsprouts.data.entity.MissedBehavior
import com.example.microsprouts.data.entity.Task
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class TaskDatabaseTest {

    private lateinit var db: MicroSproutsDatabase
    private lateinit var taskDao: TaskDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            MicroSproutsDatabase::class.java,
        ).allowMainThreadQueries().build()
        taskDao = db.taskDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun writeAndReadHierarchicalTasks() = runBlocking {
        // Create parent task
        val parentTask = Task(
            title = "Parent Plant Task",
            description = "Water the major sprout plants",
            isCompleted = false,
            startTimeOfDay = "08:00",
            recurrence = "Daily",
            missedBehavior = MissedBehavior.ADD_NEW,
        )
        val parentId = taskDao.insertTask(parentTask)

        // Create child subtask
        val childTask = Task(
            title = "Child Root Care",
            description = "Check moisture level of soil",
            isCompleted = false,
            startTimeOfDay = "08:15",
            recurrence = null,
            missedBehavior = MissedBehavior.SKIP,
            parentId = parentId,
        )
        val childId = taskDao.insertTask(childTask)

        // Query back static list
        val allTasks = taskDao.getAllTasksStatic()

        // Assert parent and child are both present and correct
        assertEquals(2, allTasks.size)

        val retrievedParent = allTasks.find { it.id == parentId }
        val retrievedChild = allTasks.find { it.id == childId }

        assertNotNull(retrievedParent)
        assertNotNull(retrievedChild)

        assertEquals("Parent Plant Task", retrievedParent!!.title)
        assertEquals("Child Root Care", retrievedChild!!.title)
        assertEquals(parentId, retrievedChild.parentId)
    }
}
