package com.example.microsprouts.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.microsprouts.data.entity.Category
import com.example.microsprouts.data.entity.MonthlyRuleType
import com.example.microsprouts.data.entity.RecurrenceBehavior
import com.example.microsprouts.data.entity.RecurrenceUnit
import com.example.microsprouts.data.entity.Task
import com.example.microsprouts.data.entity.TaskCategoryCrossRef
import com.example.microsprouts.data.entity.TaskList
import com.example.microsprouts.data.entity.YearlyRuleType
import com.example.microsprouts.data.repository.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class HomeViewModel(
    private val repository: TaskRepository,
) : ViewModel() {

    val todayTasks: StateFlow<List<Task>> = repository.allTasks
        .map { tasks -> tasks.filter { it.currentList == TaskList.TODAY } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList(),
        )

    val laterTasks: StateFlow<List<Task>> = repository.allTasks
        .map { tasks -> tasks.filter { it.currentList == TaskList.LATER } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList(),
        )

    val subtasks: StateFlow<Map<Long, List<Task>>> = repository.allTasks
        .map { tasks ->
            tasks.asSequence()
                .filter { it.parentId != null }
                .groupBy { it.parentId!! }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap(),
        )

    val allCategories: StateFlow<List<Category>> = flow {
        while (true) {
            emit(repository.getAllCategories())
            delay(3000)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList(),
    )

    val taskSecondaryCategories: StateFlow<Map<Long, List<Category>>> = repository.allTasks
        .map { tasks ->
            val map = mutableMapOf<Long, List<Category>>()
            tasks.forEach { task ->
                map[task.id] = repository.getSecondaryCategoriesForTask(task.id)
            }
            map
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap(),
        )

    init {
        runMissedBehaviorEngine()
    }

    fun insertTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertTask(task)
        }
    }

    fun insertCategory(name: String, colorHex: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertCategory(Category(name = name, colorHex = colorHex))
        }
    }

    // Standard 4-parameter insertion (for simple task creation)
    fun insertTask(
        title: String,
        primaryCategoryId: Long?,
        secondaryCategoryIds: List<Long>,
        parentId: Long?
    ) {
        insertTask(
            title = title,
            primaryCategoryId = primaryCategoryId,
            secondaryCategoryIds = secondaryCategoryIds,
            parentId = parentId,
            isRecurring = false,
            recurrenceUnit = RecurrenceUnit.DAILY,
            intervalValue = 1,
            monthlyRuleType = MonthlyRuleType.INTERVAL,
            monthlyDayOfMonth = 1,
            yearlyRuleType = YearlyRuleType.INTERVAL,
            yearlyMonth = 1,
            yearlyDayOfMonth = 1
        )
    }

    // Expanded 12-parameter insertion with full recurrence configuration
    fun insertTask(
        title: String,
        primaryCategoryId: Long?,
        secondaryCategoryIds: List<Long>,
        parentId: Long?,
        isRecurring: Boolean,
        recurrenceUnit: RecurrenceUnit,
        intervalValue: Int,
        monthlyRuleType: MonthlyRuleType,
        monthlyDayOfMonth: Int,
        yearlyRuleType: YearlyRuleType,
        yearlyMonth: Int,
        yearlyDayOfMonth: Int
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val task = Task(
                title = title,
                parentId = parentId,
                primaryCategoryId = primaryCategoryId,
                isRecurring = isRecurring,
                recurrenceUnit = recurrenceUnit,
                intervalValue = intervalValue,
                monthlyRuleType = monthlyRuleType,
                monthlyDayOfMonth = monthlyDayOfMonth,
                yearlyRuleType = yearlyRuleType,
                yearlyMonth = yearlyMonth,
                yearlyDayOfMonth = yearlyDayOfMonth,
                lastGeneratedTimestamp = System.currentTimeMillis()
            )
            val newTaskId = repository.insertTask(task)
            secondaryCategoryIds.forEach { catId ->
                repository.insertSecondaryCategory(
                    TaskCategoryCrossRef(taskId = newTaskId, categoryId = catId)
                )
            }
        }
    }

    fun updateTask(
        task: Task,
        secondaryCategoryIds: List<Long>
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertTask(task)
            repository.deleteSecondaryCategoriesForTask(task.id)
            secondaryCategoryIds.forEach { catId ->
                repository.insertSecondaryCategory(
                    TaskCategoryCrossRef(taskId = task.id, categoryId = catId)
                )
            }
        }
    }

    fun deleteTask(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTaskById(id)
            repository.deleteSecondaryCategoriesForTask(id)
        }
    }

    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertTask(task.copy(isCompleted = !task.isCompleted))
        }
    }

    fun moveTaskToToday(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            val updatedTask = task.copy(currentList = TaskList.TODAY)
            repository.insertTask(updatedTask)
        }
    }

    fun moveTaskToLater(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            val updatedTask = task.copy(currentList = TaskList.LATER)
            repository.insertTask(updatedTask)
        }
    }

    fun seedSampleData() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.seedSampleData()
        }
    }

    fun clearAllTasks() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearAllTasks()
        }
    }

    /**
     * Evaluates recurring tasks and advances them based on unit rules (Day, Week, Month, Year).
     */
    fun runMissedBehaviorEngine() {
        viewModelScope.launch(Dispatchers.IO) {
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