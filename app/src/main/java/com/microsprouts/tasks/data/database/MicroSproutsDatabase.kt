package com.microsprouts.tasks.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.microsprouts.tasks.data.dao.TaskDao
import com.microsprouts.tasks.data.entity.Category
import com.microsprouts.tasks.data.entity.Task
import com.microsprouts.tasks.data.entity.TaskCategoryCrossRef

@Database(
    entities = [
        Task::class,
        Category::class,
        TaskCategoryCrossRef::class
    ],
    version = 2,
    exportSchema = false
)
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
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}
