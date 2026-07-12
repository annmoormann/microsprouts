package com.example.microsprouts.ui.common

import androidx.compose.runtime.staticCompositionLocalOf
import com.example.microsprouts.data.repository.TaskRepository

val LocalTaskRepository = staticCompositionLocalOf<TaskRepository> {
    error("No TaskRepository provided") as TaskRepository
}
