package com.example.microsprouts.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.microsprouts.data.dao.TaskDao
import com.example.microsprouts.data.entity.Task

@Database(entities = [Task::class], version = 1, exportSchema = false)
abstract class MicroSproutsDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}
