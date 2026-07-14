package com.example.microsprouts.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = Task::class,
            parentColumns = ["id"],
            childColumns = ["parentId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["primaryCategoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["parentId"]),
        Index(value = ["primaryCategoryId"])
    ]
)
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val title: String,
    val description: String = "",
    val isCompleted: Boolean = false,
    val startTimeOfDay: String = "09:00",
    val recurrence: String? = null,
    val missedBehavior: MissedBehavior = MissedBehavior.ADD_NEW,
    val parentId: Long? = null,
    val primaryCategoryId: Long? = null
)
