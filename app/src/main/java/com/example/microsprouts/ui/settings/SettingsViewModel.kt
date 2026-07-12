package com.example.microsprouts.ui.settings

import androidx.lifecycle.ViewModel
import androidx.work.WorkManager
import com.example.microsprouts.data.repository.TaskRepository
import com.example.microsprouts.data.settings.SettingsRepository

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val repository: TaskRepository,
    private val workManager: WorkManager
) : ViewModel() {
    // Basic structural stub
}