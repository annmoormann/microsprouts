package com.example.microsprouts.ui.taskdetail

import androidx.lifecycle.ViewModel
import com.example.microsprouts.data.repository.TaskRepository
import com.example.microsprouts.data.settings.SettingsRepository

class AddEditTaskViewModel(
    private val repository: TaskRepository,
    private val settingsRepository: SettingsRepository,
    private val taskId: Long?,
    private val parentId: Long?
) : ViewModel() {
    // Basic structural stub
}