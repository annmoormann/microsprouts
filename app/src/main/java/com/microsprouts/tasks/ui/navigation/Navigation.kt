package com.microsprouts.tasks.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import androidx.room.Room
import com.microsprouts.tasks.data.database.MicroSproutsDatabase
import com.microsprouts.tasks.data.repository.TaskRepository
import com.microsprouts.tasks.ui.home.HomeScreen
import com.microsprouts.tasks.ui.home.HomeViewModel
import kotlinx.serialization.Serializable

@Serializable
sealed interface Destination : NavKey {
    @Serializable
    data object Home : Destination
}

@Composable
fun MicroSproutsNavHost(
    backStack: NavBackStack<Destination>,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current.applicationContext
    val db = remember {
        Room.databaseBuilder(
            context,
            MicroSproutsDatabase::class.java,
            "microsprouts_database",
        ).build()
    }
    val repository = remember { TaskRepository(db.taskDao()) }
    val factory = remember {
        viewModelFactory {
            initializer {
                HomeViewModel(repository)
            }
        }
    }
    val homeViewModel: HomeViewModel = viewModel(factory = factory)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<Destination.Home> {
                HomeScreen(viewModel = homeViewModel)
            }
        },
        modifier = modifier,
    )
}
