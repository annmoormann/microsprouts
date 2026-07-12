package com.example.microsprouts.ui.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.work.WorkManager
import com.example.microsprouts.data.repository.TaskRepository
import com.example.microsprouts.data.settings.SettingsRepository
import com.example.microsprouts.ui.home.HomeViewModel
import com.example.microsprouts.ui.taskdetail.AddEditTaskViewModel
import com.example.microsprouts.ui.taskdetail.TaskDetailViewModel
import com.example.microsprouts.ui.settings.SettingsViewModel

class TaskViewModelFactory(
    private val repository: TaskRepository? = null,
    private val settingsRepository: SettingsRepository? = null,
    private val workManager: WorkManager? = null,
    private val taskId: Long? = null,
    private val parentId: Long? = null
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(repository!!, settingsRepository!!) as T
            }
            modelClass.isAssignableFrom(AddEditTaskViewModel::class.java) -> {
                AddEditTaskViewModel(repository!!, settingsRepository!!, taskId, parentId) as T
            }
            modelClass.isAssignableFrom(TaskDetailViewModel::class.java) -> {
                TaskDetailViewModel(repository!!, taskId ?: 0L) as T
            }
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                SettingsViewModel(settingsRepository!!, repository!!, workManager!!) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
