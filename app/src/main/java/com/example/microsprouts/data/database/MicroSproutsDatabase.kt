package com.example.microsprouts.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.microsprouts.data.dao.TaskDao
import com.example.microsprouts.data.entity.Task

@Database(entities = [Task::class], version = 1, exportSchema = false)
abstract class MicroSproutsDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: MicroSproutsDatabase? = null

        fun getDatabase(context: Context): MicroSproutsDatabase {
            // If the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MicroSproutsDatabase::class.java,
                    "microsprouts_database"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}