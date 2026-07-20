package com.example.microsprouts.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.microsprouts.data.database.MicroSproutsDatabase
import com.example.microsprouts.data.entity.MonthlyRuleType
import com.example.microsprouts.data.entity.RecurrenceBehavior
import com.example.microsprouts.data.entity.RecurrenceUnit
import com.example.microsprouts.data.entity.Task
import com.example.microsprouts.data.entity.TaskList
import com.example.microsprouts.data.entity.YearlyRuleType
import com.example.microsprouts.data.repository.TaskRepository
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class MissedBehaviorWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val database = MicroSproutsDatabase.getDatabase(applicationContext)
            val repository = TaskRepository(database.taskDao())

            val allTasks = repository.getAllTasksRaw()
            val currentTimeMillis = System.currentTimeMillis()
            val zoneId = ZoneId.systemDefault()
            val now = ZonedDateTime.ofInstant(Instant.ofEpochMilli(currentTimeMillis), zoneId)

            allTasks.forEach { task ->
                if (task.isRecurring) {
                    val lastGen = ZonedDateTime.ofInstant(
                        Instant.ofEpochMilli(task.lastGeneratedTimestamp),
                        zoneId
                    )
                    val nextExpected = calculateNextDueDate(task, lastGen)

                    if (!now.isBefore(nextExpected)) {
                        val nextExpectedTimestamp = nextExpected.toInstant().toEpochMilli()

                        if (task.currentList == TaskList.LATER) {
                            val updatedTask = task.copy(
                                currentList = TaskList.TODAY,
                                lastGeneratedTimestamp = nextExpectedTimestamp
                            )
                            repository.insertTask(updatedTask)

                        } else if (task.currentList == TaskList.TODAY) {
                            if (!task.isCompleted) {
                                when (task.recurrenceBehavior) {
                                    RecurrenceBehavior.SKIP -> {
                                        val updatedTask = task.copy(
                                            lastGeneratedTimestamp = nextExpectedTimestamp
                                        )
                                        repository.insertTask(updatedTask)
                                    }
                                    RecurrenceBehavior.REPLACE -> {
                                        repository.deleteTask(task)
                                        val freshTask = task.copy(
                                            id = 0L,
                                            isCompleted = false,
                                            lastGeneratedTimestamp = currentTimeMillis
                                        )
                                        repository.insertTask(freshTask)
                                    }
                                    RecurrenceBehavior.STACK -> {
                                        val updatedOriginal = task.copy(
                                            lastGeneratedTimestamp = nextExpectedTimestamp
                                        )
                                        repository.insertTask(updatedOriginal)

                                        val stackedTask = task.copy(
                                            id = 0L,
                                            isCompleted = false,
                                            lastGeneratedTimestamp = currentTimeMillis
                                        )
                                        repository.insertTask(stackedTask)
                                    }
                                }
                            } else {
                                val resetTask = task.copy(
                                    isCompleted = false,
                                    lastGeneratedTimestamp = nextExpectedTimestamp
                                )
                                repository.insertTask(resetTask)
                            }
                        }
                    }
                }
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun calculateNextDueDate(task: Task, fromDate: ZonedDateTime): ZonedDateTime {
        val interval = task.intervalValue.coerceAtLeast(1).toLong()
        return when (task.recurrenceUnit) {
            RecurrenceUnit.DAILY -> fromDate.plusDays(interval)
            RecurrenceUnit.WEEKLY -> fromDate.plusWeeks(interval)
            RecurrenceUnit.MONTHLY -> {
                if (task.monthlyRuleType == MonthlyRuleType.INTERVAL) {
                    fromDate.plusMonths(interval)
                } else {
                    var targetDate = fromDate.plusMonths(interval)
                    val maxDaysInTargetMonth = targetDate.toLocalDate().lengthOfMonth()
                    val targetDay = task.monthlyDayOfMonth.coerceIn(1, maxDaysInTargetMonth)
                    targetDate = targetDate.withDayOfMonth(targetDay)

                    if (!targetDate.isAfter(fromDate)) {
                        targetDate = targetDate.plusMonths(interval)
                        val daysInNext = targetDate.toLocalDate().lengthOfMonth()
                        targetDate = targetDate.withDayOfMonth(task.monthlyDayOfMonth.coerceIn(1, daysInNext))
                    }
                    targetDate
                }
            }
            RecurrenceUnit.YEARLY -> {
                if (task.yearlyRuleType == YearlyRuleType.INTERVAL) {
                    fromDate.plusYears(interval)
                } else {
                    val targetMonth = task.yearlyMonth.coerceIn(1, 12)
                    var targetDate = fromDate.plusYears(interval).withMonth(targetMonth)
                    val targetDay = task.yearlyDayOfMonth.coerceIn(1, targetDate.toLocalDate().lengthOfMonth())
                    targetDate = targetDate.withDayOfMonth(targetDay)

                    if (!targetDate.isAfter(fromDate)) {
                        targetDate = targetDate.plusYears(interval)
                        val daysInNext = targetDate.toLocalDate().lengthOfMonth()
                        targetDate = targetDate.withDayOfMonth(task.yearlyDayOfMonth.coerceIn(1, daysInNext))
                    }
                    targetDate
                }
            }
        }
    }
}