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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.microsprouts.data.entity.Category
import com.example.microsprouts.data.entity.Task

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskSheet(
    onDismiss: () -> Unit,
    onConfirm: (
        title: String,
        primaryCategoryId: Long?,
        secondaryCategoryIds: List<Long>,
        parentId: Long?
    ) -> Unit,
    availableParentTasks: List<Task>,
    allCategories: List<Category>,
    parentSecondaryCategoriesLookup: (Long) -> List<Category>
) {
    val sheetState = rememberModalBottomSheetState()
    var title by remember { mutableStateOf("") }
    var selectedParent by remember { mutableStateOf<Task?>(null) }
    var primaryCategoryId by remember { mutableStateOf<Long?>(null) }
    val secondaryCategoryIds = remember { mutableStateListOf<Long>() }

    val filteredCategories = remember(selectedParent, allCategories) {
        if (selectedParent == null) {
            allCategories
        } else {
            val parentPrimaryId = selectedParent?.primaryCategoryId
            val parentSecondaryIds = selectedParent?.id?.let {
                parentSecondaryCategoriesLookup(it).map { cat -> cat.id }
            } ?: emptyList()
            val allowedIds = (if (parentPrimaryId != null) listOf(parentPrimaryId) else emptyList()) + parentSecondaryIds
            allCategories.filter { it.id in allowedIds }
        }
    }

    LaunchedEffect(filteredCategories) {
        if (primaryCategoryId != null && filteredCategories.none { it.id == primaryCategoryId }) {
            primaryCategoryId = null
        }
        val toRemove = secondaryCategoryIds.filter { id -> filteredCategories.none { it.id == id } }
        secondaryCategoryIds.removeAll(toRemove)
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
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Add New Task",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Task Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Parent Task Selector
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
                    availableParentTasks.forEach { task ->
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

            // Primary Category Selector
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

            // Secondary Categories checkboxes
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
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
                            selectedParent?.id
                        )
                    },
                    enabled = title.isNotBlank()
                ) {
                    Text("Confirm")
                }
            }
        }
    }
}

private fun parseHexColor(hex: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (e: Exception) {
        Color(0xFF6C8E75)
    }
}
