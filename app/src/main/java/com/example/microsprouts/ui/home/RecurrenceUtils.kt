package com.example.microsprouts.ui.home

import com.example.microsprouts.data.entity.MonthlyRuleType
import com.example.microsprouts.data.entity.RecurrenceUnit
import com.example.microsprouts.data.entity.Task
import com.example.microsprouts.data.entity.YearlyRuleType

fun getRecurrenceDisplayText(task: Task): String {
    if (!task.isRecurring) return ""

    val interval = task.intervalValue.coerceAtLeast(1)

    return when (task.recurrenceUnit) {
        RecurrenceUnit.DAILY -> {
            if (interval == 1) "Repeats daily" else "Repeats every $interval days"
        }
        RecurrenceUnit.WEEKLY -> {
            if (interval == 1) "Repeats weekly" else "Repeats every $interval weeks"
        }
        RecurrenceUnit.MONTHLY -> {
            if (task.monthlyRuleType == MonthlyRuleType.INTERVAL) {
                if (interval == 1) "Repeats monthly" else "Repeats every $interval months"
            } else {
                "Repeats monthly on day ${task.monthlyDayOfMonth}"
            }
        }
        RecurrenceUnit.YEARLY -> {
            if (task.yearlyRuleType == YearlyRuleType.INTERVAL) {
                if (interval == 1) "Repeats yearly" else "Repeats every $interval years"
            } else {
                "Repeats yearly on ${task.yearlyMonth}/${task.yearlyDayOfMonth}"
            }
        }
    }
}