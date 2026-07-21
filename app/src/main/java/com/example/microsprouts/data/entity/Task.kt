package com.example.microsprouts.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Enum representing which filtered list view the task currently belongs to.
 */
enum class TaskList {
    TODAY,
    LATER
}

/**
 * Enum mapping custom recurrence collision settings when a task recurs.
 */
enum class RecurrenceBehavior {
    SKIP,     // "Skip adding a new task"
    REPLACE,  // "Replace the current task"
    STACK     // "Add another task to the list"
}

/**
 * Time unit options for task recurrence.
 */
enum class RecurrenceUnit {
    DAILY,
    WEEKLY,
    MONTHLY,
    YEARLY
}

/**
 * Rule type for monthly task recurrence logic.
 */
enum class MonthlyRuleType {
    INTERVAL,    // "Every X months"
    SPECIFIC_DAY // "On day Y of the month"
}

/**
 * Rule type for yearly task recurrence logic.
 */
enum class YearlyRuleType {
    INTERVAL,     // "Every X years"
    SPECIFIC_DATE // "On Month X, Day Y"
}

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val title: String,
    val description: String = "",
    val isCompleted: Boolean = false,

    // Filter tag determining list view membership ("Today" vs "Later")
    val currentList: TaskList = TaskList.TODAY,

    // Recurrence configuration
    val isRecurring: Boolean = false,
    val intervalDays: Int = 1, // e.g., 1 for Daily, 7 for Weekly
    val recurrenceBehavior: RecurrenceBehavior = RecurrenceBehavior.SKIP,

    // Date generation tracking for calculating interval progression
    val lastGeneratedTimestamp: Long = System.currentTimeMillis(),

    // Advanced recurrence metadata
    val recurrenceUnit: RecurrenceUnit = RecurrenceUnit.DAILY,
    val intervalValue: Int = 1,
    val monthlyRuleType: MonthlyRuleType = MonthlyRuleType.INTERVAL,
    val monthlyDayOfMonth: Int = 1,
    val yearlyRuleType: YearlyRuleType = YearlyRuleType.INTERVAL,
    val yearlyMonth: Int = 1,
    val yearlyDayOfMonth: Int = 1,

    // Relationships
    val parentId: Long? = null,
    val primaryCategoryId: Long? = null
)