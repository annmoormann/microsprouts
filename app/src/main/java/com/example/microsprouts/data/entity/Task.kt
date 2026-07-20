package com.example.microsprouts.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// enum representing where the task physically sits right now
enum class TaskList { TODAY, LATER }

// enum mapping your custom recurrence collision settings
enum class RecurrenceBehavior {
    SKIP,     // "Skip adding a new task"
    REPLACE,  // "Replace the current task"
    STACK     // "Add another task to the list"
}

// New Enums for advanced intervals
enum class RecurrenceUnit {
    DAILY, WEEKLY, MONTHLY, YEARLY
}

enum class MonthlyRuleType {
    INTERVAL,    // "Every X months"
    SPECIFIC_DAY // "On day Y of the month"
}

enum class YearlyRuleType {
    INTERVAL,     // "Every X years"
    SPECIFIC_DATE // "On Month X, Day Y"
}

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val description: String = "",
    val isCompleted: Boolean = false,

    // Explicitly tracks which list was open when created/moved
    val currentList: TaskList = TaskList.TODAY,

    // Recurrence configuration
    val isRecurring: Boolean = false,
    val intervalDays: Int = 1, // e.g., 1 for Daily, 7 for Weekly
    val recurrenceBehavior: RecurrenceBehavior = RecurrenceBehavior.SKIP,

    // Tracing date generation so we can calculate when intervals pass
    val lastGeneratedTimestamp: Long = System.currentTimeMillis(),

    // Advanced recurrence metadata fields
    val recurrenceUnit: RecurrenceUnit = RecurrenceUnit.DAILY,
    val intervalValue: Int = 1,
    val monthlyRuleType: MonthlyRuleType = MonthlyRuleType.INTERVAL,
    val monthlyDayOfMonth: Int = 1,
    val yearlyRuleType: YearlyRuleType = YearlyRuleType.INTERVAL,
    val yearlyMonth: Int = 1,
    val yearlyDayOfMonth: Int = 1,

    val parentId: Long? = null,
    val primaryCategoryId: Long? = null,
)