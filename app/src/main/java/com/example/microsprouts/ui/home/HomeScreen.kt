package com.example.microsprouts.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.microsprouts.R
import com.example.microsprouts.data.entity.Task

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Today", "Later")
    val todayTasks by viewModel.todayTasks.collectAsStateWithLifecycle()
    val laterTasks by viewModel.laterTasks.collectAsStateWithLifecycle()
    val subtasks by viewModel.subtasks.collectAsStateWithLifecycle()
    val allCategories by viewModel.allCategories.collectAsStateWithLifecycle()
    val taskSecondaryCategories by viewModel.taskSecondaryCategories.collectAsStateWithLifecycle()

    var showAddTaskSheet by remember { mutableStateOf(false) }
    var editingTask by remember { mutableStateOf<Task?>(null) }

    val activeTasks = if (selectedTab == 0) todayTasks else laterTasks

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                ),
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.app_logo),
                            contentDescription = "MicroSprouts Logo",
                            modifier = Modifier.height(36.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "MicroSprouts",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.seedSampleData() },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Seed Sample Data",
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                    IconButton(
                        onClick = { viewModel.clearAllTasks() },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Clear All Tasks",
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddTaskSheet = true },
                containerColor = Color(0xFF6C8E75),
                contentColor = Color.White,
                shape = CircleShape,
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Task",
                )
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = MaterialTheme.colorScheme.primary,
                    )
                },
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 16.sp,
                            )
                        },
                    )
                }
            }

            if (activeTasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = if (selectedTab == 0) "No tasks for today!" else "No tasks scheduled for later!",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        fontSize = 16.sp,
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(activeTasks, key = { it.id }) { parentTask ->
                        val taskSubtasks = subtasks[parentTask.id] ?: emptyList()

                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = { dismissValue ->
                                when (dismissValue) {
                                    SwipeToDismissBoxValue.StartToEnd -> {
                                        if (selectedTab == 0) {
                                            viewModel.moveTaskToLater(parentTask)
                                            true
                                        } else {
                                            false
                                        }
                                    }
                                    SwipeToDismissBoxValue.EndToStart -> {
                                        if (selectedTab == 1) {
                                            viewModel.moveTaskToToday(parentTask)
                                            true
                                        } else {
                                            false
                                        }
                                    }
                                    else -> false
                                }
                            },
                        )

                        SwipeToDismissBox(
                            state = dismissState,
                            enableDismissFromStartToEnd = (selectedTab == 0),
                            enableDismissFromEndToStart = (selectedTab == 1),
                            backgroundContent = {
                                val color = when (dismissState.dismissDirection) {
                                    SwipeToDismissBoxValue.StartToEnd -> Color(0xFFE6EFE9)
                                    SwipeToDismissBoxValue.EndToStart -> Color(0xFFEBF3FC)
                                    else -> Color.Transparent
                                }
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(color, shape = RoundedCornerShape(16.dp))
                                        .padding(horizontal = 24.dp),
                                    contentAlignment = if (dismissState.dismissDirection == SwipeToDismissBoxValue.StartToEnd) {
                                        Alignment.CenterStart
                                    } else {
                                        Alignment.CenterEnd
                                    },
                                ) {
                                    Text(
                                        text = if (dismissState.dismissDirection == SwipeToDismissBoxValue.StartToEnd) {
                                            "Move to Later ➔"
                                        } else {
                                            "⇠ Move to Today"
                                        },
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp,
                                    )
                                }
                            },
                        ) {
                            ParentTaskCard(
                                task = parentTask,
                                subtasks = taskSubtasks,
                                allCategories = allCategories,
                                secondaryCategories = taskSecondaryCategories[parentTask.id] ?: emptyList(),
                                onToggle = { viewModel.toggleTaskCompletion(it) },
                                onCardClick = { editingTask = parentTask },
                                onSubtaskClick = { editingTask = it },
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddTaskSheet) {
        AddTaskSheet(
            onDismiss = { showAddTaskSheet = false },
            onConfirm = { title, primaryCategoryId, secondaryCategoryIds, parentId,
                          isRecurring, recurrenceUnit, intervalValue, monthlyRuleType,
                          monthlyDayOfMonth, yearlyRuleType, yearlyMonth, yearlyDayOfMonth ->
                viewModel.insertTask(
                    title = title,
                    primaryCategoryId = primaryCategoryId,
                    secondaryCategoryIds = secondaryCategoryIds,
                    parentId = parentId,
                    isRecurring = isRecurring,
                    recurrenceUnit = recurrenceUnit,
                    intervalValue = intervalValue,
                    monthlyRuleType = monthlyRuleType,
                    monthlyDayOfMonth = monthlyDayOfMonth,
                    yearlyRuleType = yearlyRuleType,
                    yearlyMonth = yearlyMonth,
                    yearlyDayOfMonth = yearlyDayOfMonth,
                )
                showAddTaskSheet = false
            },
            availableParentTasks = todayTasks + laterTasks,
            allCategories = allCategories,
            parentSecondaryCategoriesLookup = { parentId ->
                taskSecondaryCategories[parentId] ?: emptyList()
            },
            onCreateCategory = { name, colorHex ->
                viewModel.insertCategory(name, colorHex)
            },
        )
    }

    if (editingTask != null) {
        val currentTask = editingTask!!
        EditTaskSheet(
            task = currentTask,
            onDismiss = { editingTask = null },
            onConfirm = { title, primaryCategoryId, secondaryCategoryIds, parentId,
                          isRecurring, recurrenceUnit, intervalValue, monthlyRuleType,
                          monthlyDayOfMonth, yearlyRuleType, yearlyMonth, yearlyDayOfMonth,
                          recurrenceBehavior ->
                viewModel.updateTask(
                    currentTask.copy(
                        title = title,
                        primaryCategoryId = primaryCategoryId,
                        parentId = parentId,
                        isRecurring = isRecurring,
                        recurrenceUnit = recurrenceUnit,
                        intervalValue = intervalValue,
                        monthlyRuleType = monthlyRuleType,
                        monthlyDayOfMonth = monthlyDayOfMonth,
                        yearlyRuleType = yearlyRuleType,
                        yearlyMonth = yearlyMonth,
                        yearlyDayOfMonth = yearlyDayOfMonth,
                        recurrenceBehavior = recurrenceBehavior
                    ),
                    secondaryCategoryIds
                )
                editingTask = null
            },
            onDelete = {
                viewModel.deleteTask(currentTask.id)
                editingTask = null
            },
            availableParentTasks = todayTasks + laterTasks,
            allCategories = allCategories,
            currentSecondaryCategoryIds = taskSecondaryCategories[currentTask.id]?.map { it.id } ?: emptyList(),
            parentSecondaryCategoriesLookup = { parentId ->
                taskSecondaryCategories[parentId] ?: emptyList()
            },
            onCreateCategory = { name, colorHex ->
                viewModel.insertCategory(name, colorHex)
            }
        )
    }
}