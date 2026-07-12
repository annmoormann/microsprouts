package com.example.microsprouts.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.runtime.entryProvider
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import com.example.microsprouts.ui.home.HomeScreen
import com.example.microsprouts.ui.taskdetail.AddEditTaskScreen
import com.example.microsprouts.ui.taskdetail.TaskDetailScreen
import com.example.microsprouts.ui.settings.SettingsScreen

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun MicroSproutsNavHost(
    backStack: NavBackStack<Destination>
) {
    val strategy = rememberListDetailSceneStrategy<Destination>()

    val entryProvider = entryProvider<Destination> {
        addEntryProvider(
            Destination.Home,
            metadata = ListDetailSceneStrategy.listPane()
        ) {
            HomeScreen(
                onNavigateToDetail = { taskId ->
                    backStack.add(Destination.TaskDetail(taskId))
                },
                onNavigateToAdd = {
                    backStack.add(Destination.AddEditTask())
                },
                onNavigateToSettings = {
                    backStack.add(Destination.Settings)
                }
            )
        }
        addEntryProvider(
            Destination.TaskDetail::class,
            metadata = ListDetailSceneStrategy.detailPane()
        ) { destination ->
            TaskDetailScreen(
                taskId = destination.taskId,
                onNavigateBack = { backStack.removeAt(backStack.lastIndex) },
                onEditTask = { taskId ->
                    backStack.add(Destination.AddEditTask(taskId = taskId))
                },
                onAddSubtask = { parentId ->
                    backStack.add(Destination.AddEditTask(parentId = parentId))
                }
            )
        }
        addEntryProvider(
            Destination.AddEditTask::class,
            metadata = ListDetailSceneStrategy.extraPane()
        ) { destination ->
            AddEditTaskScreen(
                taskId = destination.taskId,
                parentId = destination.parentId,
                onNavigateBack = { backStack.removeAt(backStack.lastIndex) }
            )
        }
        addEntryProvider(
            Destination.Settings,
            metadata = ListDetailSceneStrategy.extraPane()
        ) {
            SettingsScreen(
                onNavigateBack = { backStack.removeAt(backStack.lastIndex) }
            )
        }
    }

    val decorators = listOf(
        rememberSaveableStateHolderNavEntryDecorator<Destination>(),
        rememberViewModelStoreNavEntryDecorator<Destination>()
    )

    NavDisplay(
        backStack = backStack,
        entryProvider = entryProvider,
        entryDecorators = decorators,
        sceneStrategy = strategy,
        modifier = Modifier.fillMaxSize(),
        onBack = { backStack.removeAt(backStack.lastIndex) }
    )
}
