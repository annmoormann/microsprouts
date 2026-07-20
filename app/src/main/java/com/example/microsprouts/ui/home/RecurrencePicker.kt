package com.example.microsprouts.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.microsprouts.data.entity.MonthlyRuleType
import com.example.microsprouts.data.entity.RecurrenceBehavior
import com.example.microsprouts.data.entity.RecurrenceUnit
import com.example.microsprouts.data.entity.YearlyRuleType

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
    onYearlyDayOfMonthChange: (Int) -> Unit,
    recurrenceBehavior: RecurrenceBehavior,
    onRecurrenceBehaviorChange: (RecurrenceBehavior) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Checkbox(
                checked = isRecurring,
                onCheckedChange = onIsRecurringChange
            )
            Text(
                text = "Repeat Task",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        AnimatedVisibility(
            visible = isRecurring,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(
                modifier = Modifier.padding(start = 12.dp, top = 8.dp, bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Unit Selection Chips
                Text("Frequency", style = MaterialTheme.typography.labelLarge)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    RecurrenceUnit.entries.forEach { unit ->
                        FilterChip(
                            selected = recurrenceUnit == unit,
                            onClick = { onRecurrenceUnitChange(unit) },
                            label = { Text(unit.name.lowercase().replaceFirstChar { it.uppercase() }) }
                        )
                    }
                }

                // Monthly Rules Sub-Toggle
                if (recurrenceUnit == RecurrenceUnit.MONTHLY) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = monthlyRuleType == MonthlyRuleType.INTERVAL,
                            onClick = { onMonthlyRuleTypeChange(MonthlyRuleType.INTERVAL) },
                            label = { Text("Every X Months") }
                        )
                        FilterChip(
                            selected = monthlyRuleType == MonthlyRuleType.SPECIFIC_DAY,
                            onClick = { onMonthlyRuleTypeChange(MonthlyRuleType.SPECIFIC_DAY) },
                            label = { Text("Specific Day") }
                        )
                    }
                }

                // Yearly Rules Sub-Toggle
                if (recurrenceUnit == RecurrenceUnit.YEARLY) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = yearlyRuleType == YearlyRuleType.INTERVAL,
                            onClick = { onYearlyRuleTypeChange(YearlyRuleType.INTERVAL) },
                            label = { Text("Every X Years") }
                        )
                        FilterChip(
                            selected = yearlyRuleType == YearlyRuleType.SPECIFIC_DATE,
                            onClick = { onYearlyRuleTypeChange(YearlyRuleType.SPECIFIC_DATE) },
                            label = { Text("Specific Date") }
                        )
                    }
                }

                // Interval Input (Shown for Daily, Weekly, or Interval-based Monthly/Yearly)
                val showIntervalInput = when (recurrenceUnit) {
                    RecurrenceUnit.DAILY, RecurrenceUnit.WEEKLY -> true
                    RecurrenceUnit.MONTHLY -> monthlyRuleType == MonthlyRuleType.INTERVAL
                    RecurrenceUnit.YEARLY -> yearlyRuleType == YearlyRuleType.INTERVAL
                }

                if (showIntervalInput) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Every", style = MaterialTheme.typography.bodyMedium)
                        OutlinedTextField(
                            value = intervalValue.toString(),
                            onValueChange = { newValue ->
                                newValue.toIntOrNull()?.let { onIntervalValueChange(it.coerceAtLeast(1)) }
                            },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        Text(
                            text = when (recurrenceUnit) {
                                RecurrenceUnit.DAILY -> if (intervalValue > 1) "days" else "day"
                                RecurrenceUnit.WEEKLY -> if (intervalValue > 1) "weeks" else "week"
                                RecurrenceUnit.MONTHLY -> if (intervalValue > 1) "months" else "month"
                                RecurrenceUnit.YEARLY -> if (intervalValue > 1) "years" else "year"
                            },
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                // Specific Day Inputs
                if (recurrenceUnit == RecurrenceUnit.MONTHLY && monthlyRuleType == MonthlyRuleType.SPECIFIC_DAY) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Day of month: ", style = MaterialTheme.typography.bodyMedium)
                        OutlinedTextField(
                            value = monthlyDayOfMonth.toString(),
                            onValueChange = { newDay ->
                                newDay.toIntOrNull()?.let { onMonthlyDayOfMonthChange(it.coerceIn(1, 31)) }
                            },
                            singleLine = true
                        )
                    }
                }

                // Specific Date Inputs
                if (recurrenceUnit == RecurrenceUnit.YEARLY && yearlyRuleType == YearlyRuleType.SPECIFIC_DATE) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Month:", style = MaterialTheme.typography.bodyMedium)
                        OutlinedTextField(
                            value = yearlyMonth.toString(),
                            onValueChange = { m ->
                                m.toIntOrNull()?.let { onYearlyMonthChange(it.coerceIn(1, 12)) }
                            },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        Text("Day:", style = MaterialTheme.typography.bodyMedium)
                        OutlinedTextField(
                            value = yearlyDayOfMonth.toString(),
                            onValueChange = { d ->
                                d.toIntOrNull()?.let { onYearlyDayOfMonthChange(it.coerceIn(1, 31)) }
                            },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Recurrence Behavior (SKIP / REPLACE / STACK)
                Text("When due date passes:", style = MaterialTheme.typography.labelLarge)
                Column {
                    RecurrenceBehavior.entries.forEach { behavior ->
                        val labelText = when (behavior) {
                            RecurrenceBehavior.SKIP -> "Skip (Move due date to next interval)"
                            RecurrenceBehavior.REPLACE -> "Replace (Overwrites current unfinished task)"
                            RecurrenceBehavior.STACK -> "Stack (Creates an additional task copy)"
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = (recurrenceBehavior == behavior),
                                    onClick = { onRecurrenceBehaviorChange(behavior) }
                                )
                                .padding(vertical = 4.dp)
                        ) {
                            RadioButton(
                                selected = (recurrenceBehavior == behavior),
                                onClick = { onRecurrenceBehaviorChange(behavior) }
                            )
                            Text(
                                text = labelText,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}