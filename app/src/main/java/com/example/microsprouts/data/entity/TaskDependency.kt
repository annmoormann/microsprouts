package com.example.microsprouts.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "task_dependencies",
    primaryKeys = ["taskId", "blockedById"],
    foreignKeys = [
        ForeignKey(
            entity = Task::class,
            parentColumns = ["id"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Task::class,
            parentColumns = ["id"],
            childColumns = ["blockedById"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TaskDependency(
    val taskId: Long,
    val blockedById: Long
)