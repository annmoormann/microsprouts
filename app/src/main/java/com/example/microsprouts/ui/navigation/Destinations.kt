package com.example.microsprouts.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Destination : NavKey {
    @Serializable
    data object Home : Destination

    @Serializable
    data class TaskDetail(val taskId: Long) : Destination

    @Serializable
    data class AddEditTask(val taskId: Long? = null, val parentId: Long? = null) : Destination

    @Serializable
    data object Settings : Destination
}
