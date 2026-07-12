package com.example.microsprouts.data.entity

enum class MissedBehavior {
    ADD_NEW, REPLACE, SKIP
}

data class Task(
    val id: Long = 0L,
    val title: String,
    val description: String = "",
    val isCompleted: Boolean = false,
    val startTimeOfDay: String = "09:00",
    val recurrence: String? = null, // <-- Add this line right here!
    val missedBehavior: MissedBehavior = MissedBehavior.ADD_NEW,
    val parentId: Long? = null
)