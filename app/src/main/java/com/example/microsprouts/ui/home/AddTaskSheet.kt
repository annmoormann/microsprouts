package com.example.microsprouts.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.microsprouts.data.entity.Category
import com.example.microsprouts.data.entity.MonthlyRuleType
import com.example.microsprouts.data.entity.RecurrenceBehavior
import com.example.microsprouts.data.entity.RecurrenceUnit
import com.example.microsprouts.data.entity.Task
import com.example.microsprouts.data.entity.YearlyRuleType
import com.example.microsprouts.ui.components.RecurrencePicker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskSheet(
    onDismiss: () -> Unit,
    onConfirm: (
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
    ) -> Unit,
    availableParentTasks: List<Task>,
    allCategories: List<Category>,
    parentSecondaryCategoriesLookup: (Long) -> List<Category>,
    onCreateCategory: (name: String, colorHex: String) -> Unit = { _, _ -> }
) {
    val sheetState = rememberModalBottomSheetState()
    var title by remember { mutableStateOf("") }
    var selectedParent by remember { mutableStateOf<Task?>(null) }
    var primaryCategoryId by remember { mutableStateOf<Long?>(null) }
    val secondaryCategoryIds = remember { mutableStateListOf<Long>() }

    var showCreateCategoryDialog by remember { mutableStateOf(false) }

    // Recurrence State Tracking Variables
    var isRecurring by remember { mutableStateOf(false) }
    var recurrenceUnit by remember { mutableStateOf(RecurrenceUnit.DAILY) }
    var intervalValue by remember { mutableIntStateOf(1) }
    var monthlyRuleType by remember { mutableStateOf(MonthlyRuleType.INTERVAL) }
    var monthlyDayOfMonth by remember { mutableIntStateOf(1) }
    var yearlyRuleType by remember { mutableStateOf(YearlyRuleType.INTERVAL) }
    var yearlyMonth by remember { mutableIntStateOf(1) }
    var yearlyDayOfMonth by remember { mutableIntStateOf(1) }
    var recurrenceBehavior by remember { mutableStateOf(RecurrenceBehavior.SKIP) }

    // 1. Filtered Categories derived from parent inheritance
    val filteredCategories = remember(selectedParent, allCategories) {
        val parent = selectedParent
        if (parent == null) {
            allCategories
        } else {
            val parentPrimaryId = parent.primaryCategoryId
            val parentSecondaryIds = parentSecondaryCategoriesLookup(parent.id).map { cat -> cat.id }
            val allowedIds = (if (parentPrimaryId != null) listOf(parentPrimaryId) else emptyList()) + parentSecondaryIds
            allCategories.filter { it.id in allowedIds }
        }
    }

    // 2. Filtered Parent Tasks matching selected categories
    val filteredParentTasks = remember(primaryCategoryId, secondaryCategoryIds.size, secondaryCategoryIds.firstOrNull(), availableParentTasks) {
        availableParentTasks.filter { parent ->
            val parentPrimaryId = parent.primaryCategoryId
            val parentSecondaryIds = parentSecondaryCategoriesLookup(parent.id).map { it.id }
            val parentAllCategoryIds = (if (parentPrimaryId != null) listOf(parentPrimaryId) else emptyList()) + parentSecondaryIds

            val matchesPrimary = primaryCategoryId == null || primaryCategoryId in parentAllCategoryIds
            val matchesSecondary = secondaryCategoryIds.all { it in parentAllCategoryIds }

            matchesPrimary && matchesSecondary
        }
    }

    // Cleanup selections that become invalid when selectedParent changes
    LaunchedEffect(selectedParent) {
        if (primaryCategoryId != null && filteredCategories.none { it.id == primaryCategoryId }) {
            primaryCategoryId = null
        }
        val toRemove = secondaryCategoryIds.filter { id -> filteredCategories.none { it.id == id } }
        if (toRemove.isNotEmpty()) {
            secondaryCategoryIds.removeAll(toRemove)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Add New Task",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            // 1. Task Title Input
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Task Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // 2. Primary Category Selector
            var primaryDropdownExpanded by remember { mutableStateOf(false) }
            val selectedPrimaryCategoryName = filteredCategories.find { it.id == primaryCategoryId }?.name ?: "No Primary Category"
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = selectedPrimaryCategoryName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Primary Category") },
                    trailingIcon = {
                        IconButton(onClick = { primaryDropdownExpanded = true }) {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Select Primary Category"
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { primaryDropdownExpanded = true }
                )
                DropdownMenu(
                    expanded = primaryDropdownExpanded,
                    onDismissRequest = { primaryDropdownExpanded = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "+ Create New Category",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        onClick = {
                            showCreateCategoryDialog = true
                            primaryDropdownExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("No Primary Category") },
                        onClick = {
                            primaryCategoryId = null
                            primaryDropdownExpanded = false
                        }
                    )
                    filteredCategories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.name) },
                            onClick = {
                                primaryCategoryId = category.id
                                primaryDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            // 3. Secondary Categories Checkboxes
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Secondary Categories",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (filteredCategories.isEmpty()) {
                    Text(
                        text = "No categories available",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 140.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        filteredCategories.forEach { category ->
                            val isChecked = secondaryCategoryIds.contains(category.id)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (isChecked) {
                                            secondaryCategoryIds.remove(category.id)
                                        } else {
                                            secondaryCategoryIds.add(category.id)
                                        }
                                    }
                                    .padding(vertical = 4.dp)
                            ) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = { checked ->
                                        if (checked) {
                                            secondaryCategoryIds.add(category.id)
                                        } else {
                                            secondaryCategoryIds.remove(category.id)
                                        }
                                    }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .background(
                                            color = parseHexColor(category.colorHex),
                                            shape = CircleShape
                                        )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = category.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            // 4. Parent Task Selector
            var parentDropdownExpanded by remember { mutableStateOf(false) }
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = selectedParent?.title ?: "No Parent (Main Task)",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Parent Task") },
                    trailingIcon = {
                        IconButton(onClick = { parentDropdownExpanded = true }) {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Select Parent Task"
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { parentDropdownExpanded = true }
                )
                DropdownMenu(
                    expanded = parentDropdownExpanded,
                    onDismissRequest = { parentDropdownExpanded = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    DropdownMenuItem(
                        text = { Text("No Parent (Main Task)") },
                        onClick = {
                            selectedParent = null
                            parentDropdownExpanded = false
                        }
                    )
                    filteredParentTasks.forEach { task ->
                        DropdownMenuItem(
                            text = { Text(task.title) },
                            onClick = {
                                selectedParent = task
                                parentDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            // 5. Dynamic Recurrence Picker Component
            RecurrencePicker(
                isRecurring = isRecurring,
                onIsRecurringChange = { isRecurring = it },
                recurrenceUnit = recurrenceUnit,
                onRecurrenceUnitChange = { recurrenceUnit = it },
                intervalValue = intervalValue,
                onIntervalValueChange = { intervalValue = it },
                monthlyRuleType = monthlyRuleType,
                onMonthlyRuleTypeChange = { monthlyRuleType = it },
                monthlyDayOfMonth = monthlyDayOfMonth,
                onMonthlyDayOfMonthChange = { monthlyDayOfMonth = it },
                yearlyRuleType = yearlyRuleType,
                onYearlyRuleTypeChange = { yearlyRuleType = it },
                yearlyMonth = yearlyMonth,
                onYearlyMonthChange = { yearlyMonth = it },
                yearlyDayOfMonth = yearlyDayOfMonth,
                onYearlyDayOfMonthChange = { yearlyDayOfMonth = it },
                recurrenceBehavior = recurrenceBehavior,
                onRecurrenceBehaviorChange = { recurrenceBehavior = it }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(onClick = onDismiss) {
                    Text("Cancel")
                }
                Spacer(modifier = Modifier.width(12.dp))
                Button(
                    onClick = {
                        onConfirm(
                            title.trim(),
                            primaryCategoryId,
                            secondaryCategoryIds.toList(),
                            selectedParent?.id,
                            isRecurring,
                            recurrenceUnit,
                            intervalValue,
                            monthlyRuleType,
                            monthlyDayOfMonth,
                            yearlyRuleType,
                            yearlyMonth,
                            yearlyDayOfMonth
                        )
                    },
                    enabled = title.isNotBlank()
                ) {
                    Text("Confirm")
                }
            }
        }
    }

    // Inline category creation dialog (Default Sage Green color)
    if (showCreateCategoryDialog) {
        var newCategoryName by remember { mutableStateOf("") }
        val defaultBrandColorHex = "#6C8E75"

        AlertDialog(
            onDismissRequest = { showCreateCategoryDialog = false },
            title = {
                Text(
                    text = "Create New Category",
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = newCategoryName,
                        onValueChange = { newCategoryName = it },
                        label = { Text("Category Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newCategoryName.isNotBlank()) {
                            onCreateCategory(newCategoryName.trim(), defaultBrandColorHex)
                            showCreateCategoryDialog = false
                        }
                    },
                    enabled = newCategoryName.isNotBlank()
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showCreateCategoryDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

private fun parseHexColor(hex: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (e: Exception) {
        Color(0xFF6C8E75)
    }
}