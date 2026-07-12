package com.example.microsprouts.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class MissedBehavior {
    ADD_NEW, REPLACE, SKIP
}

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val title: String,
    val description: String = "",
    val isCompleted: Boolean = false,
    val startTimeOfDay: String = "09:00",
    val missedBehavior: MissedBehavior = MissedBehavior.SKIP,
    val parentId: Long? = null
)