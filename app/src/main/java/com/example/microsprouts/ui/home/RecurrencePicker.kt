package com.example.microsprouts.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.microsprouts.data.entity.MonthlyRuleType
import com.example.microsprouts.data.entity.RecurrenceUnit
import com.example.microsprouts.data.entity.YearlyRuleType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecurrencePicker(
    isRecurring: Boolean,
    onIsRecurringChange: (Boolean) -> Unit,
    recurrenceUnit: RecurrenceUnit,
    onRecurrenceUnitChange: (RecurrenceUnit) -> Unit,
    intervalValue: Int,
    onIntervalValueChange: (Int) -> Unit,
    monthlyRuleType: MonthlyRuleType,
    onMonthlyRuleTypeChange: (MonthlyRuleType) -> Unit,
    monthlyDayOfMonth: Int,
    onMonthlyDayOfMonthChange: (Int) -> Unit,
    yearlyRuleType: YearlyRuleType,
    onYearlyRuleTypeChange: (YearlyRuleType) -> Unit,
    yearlyMonth: Int,
    onYearlyMonthChange: (Int) -> Unit,
    yearlyDayOfMonth: Int,
    onYearlyDayOfMonthChange: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {

        // 1. Standalone Checkbox: Toggles between One-Off and Repeating
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)
        ) {
            Checkbox(
                checked = isRecurring,
                onCheckedChange = onIsRecurringChange
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Repeat Task", style = MaterialTheme.typography.bodyLarge)
        }

        // 2. Hidden Content Section (Only slides open if checked)
        AnimatedVisibility(
            visible = isRecurring,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column(modifier = Modifier.padding(start = 32.dp, top = 4.dp)) {

                // Interval Unit Selection Row
                Text(
                    text = "Recurrence Interval",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RecurrenceUnit.values().forEach { unit ->
                        FilterChip(
                            selected = recurrenceUnit == unit,
                            onClick = { onRecurrenceUnitChange(unit) },
                            label = { Text(unit.name.lowercase().replaceFirstChar { it.uppercase() }) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 3. Conditional Visibility for Sub-Options
                when (recurrenceUnit) {
                    RecurrenceUnit.DAILY -> {
                        StandardIntervalInput(label = "days", value = intervalValue, onValueChange = onIntervalValueChange)
                    }
                    RecurrenceUnit.WEEKLY -> {
                        StandardIntervalInput(label = "weeks", value = intervalValue, onValueChange = onIntervalValueChange)
                    }
                    RecurrenceUnit.MONTHLY -> {
                        MonthlyOptions(
                            intervalValue = intervalValue,
                            onIntervalValueChange = onIntervalValueChange,
                            monthlyRuleType = monthlyRuleType,
                            onMonthlyRuleTypeChange = onMonthlyRuleTypeChange,
                            monthlyDayOfMonth = monthlyDayOfMonth,
                            onMonthlyDayOfMonthChange = onMonthlyDayOfMonthChange
                        )
                    }
                    RecurrenceUnit.YEARLY -> {
                        YearlyOptions(
                            intervalValue = intervalValue,
                            onIntervalValueChange = onIntervalValueChange,
                            yearlyRuleType = yearlyRuleType,
                            onYearlyRuleTypeChange = onYearlyRuleTypeChange,
                            yearlyMonth = yearlyMonth,
                            onYearlyMonthChange = onYearlyMonthChange,
                            yearlyDayOfMonth = yearlyDayOfMonth,
                            onYearlyDayOfMonthChange = onYearlyDayOfMonthChange
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StandardIntervalInput(label: String, value: Int, onValueChange: (Int) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("Every ", style = MaterialTheme.typography.bodyMedium)
        OutlinedTextField(
            value = if (value == 0) "" else value.toString(),
            onValueChange = { newValue ->
                onValueChange(newValue.filter { it.isDigit() }.toIntOrNull() ?: 1)
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.width(70.dp).padding(horizontal = 4.dp),
            singleLine = true
        )
        Text(" $label", style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun MonthlyOptions(
    intervalValue: Int,
    onIntervalValueChange: (Int) -> Unit,
    monthlyRuleType: MonthlyRuleType,
    onMonthlyRuleTypeChange: (MonthlyRuleType) -> Unit,
    monthlyDayOfMonth: Int,
    onMonthlyDayOfMonthChange: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = monthlyRuleType == MonthlyRuleType.INTERVAL,
                onClick = { onMonthlyRuleTypeChange(MonthlyRuleType.INTERVAL) }
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("By interval", style = MaterialTheme.typography.bodyMedium)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = monthlyRuleType == MonthlyRuleType.SPECIFIC_DAY,
                onClick = { onMonthlyRuleTypeChange(MonthlyRuleType.SPECIFIC_DAY) }
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("On specific day of month", style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Nest the secondary reveal based on radio choices
        if (monthlyRuleType == MonthlyRuleType.INTERVAL) {
            StandardIntervalInput(label = "months", value = intervalValue, onValueChange = onIntervalValueChange)
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("On day ", style = MaterialTheme.typography.bodyMedium)
                OutlinedTextField(
                    value = monthlyDayOfMonth.toString(),
                    onValueChange = { newValue ->
                        val day = newValue.filter { it.isDigit() }.toIntOrNull() ?: 1
                        onMonthlyDayOfMonthChange(day.coerceIn(1, 31))
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.width(70.dp).padding(horizontal = 4.dp),
                    singleLine = true
                )
                Text(" of the month", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun YearlyOptions(
    intervalValue: Int,
    onIntervalValueChange: (Int) -> Unit,
    yearlyRuleType: YearlyRuleType,
    onYearlyRuleTypeChange: (YearlyRuleType) -> Unit,
    yearlyMonth: Int,
    onYearlyMonthChange: (Int) -> Unit,
    yearlyDayOfMonth: Int,
    onYearlyDayOfMonthChange: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = yearlyRuleType == YearlyRuleType.INTERVAL,
                onClick = { onYearlyRuleTypeChange(YearlyRuleType.INTERVAL) }
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("By interval", style = MaterialTheme.typography.bodyMedium)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = yearlyRuleType == YearlyRuleType.SPECIFIC_DATE,
                onClick = { onYearlyRuleTypeChange(YearlyRuleType.SPECIFIC_DATE) }
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("On specific date each year", style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Nest the secondary reveal based on radio choices
        if (yearlyRuleType == YearlyRuleType.INTERVAL) {
            StandardIntervalInput(label = "years", value = intervalValue, onValueChange = onIntervalValueChange)
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("On:", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = yearlyMonth.toString(),
                    onValueChange = { newValue ->
                        val month = newValue.filter { it.isDigit() }.toIntOrNull() ?: 1
                        onYearlyMonthChange(month.coerceIn(1, 12))
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.width(60.dp),
                    singleLine = true,
                    label = { Text("Month") }
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("/", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.width(4.dp))
                OutlinedTextField(
                    value = yearlyDayOfMonth.toString(),
                    onValueChange = { newValue ->
                        val day = newValue.filter { it.isDigit() }.toIntOrNull() ?: 1
                        onYearlyDayOfMonthChange(day.coerceIn(1, 31))
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.width(60.dp),
                    singleLine = true,
                    label = { Text("Day") }
                )
            }
        }
    }
}