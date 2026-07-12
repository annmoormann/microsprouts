package com.example.microsprouts.ui.home

import androidx.lifecycle.ViewModel
import com.example.microsprouts.data.repository.TaskRepository
import com.example.microsprouts.data.settings.SettingsRepository

class HomeViewModel(
    private val repository: TaskRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    // Basic structural stub
}