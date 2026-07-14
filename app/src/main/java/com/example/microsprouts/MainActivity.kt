package com.example.microsprouts

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.microsprouts.data.database.MicroSproutsDatabase
import com.example.microsprouts.data.repository.TaskRepository
import com.example.microsprouts.ui.home.HomeScreen
import com.example.microsprouts.ui.home.HomeViewModel
import com.example.microsprouts.ui.home.HomeViewModelFactory // If you have a factory, otherwise use dynamic creation
import com.example.microsprouts.ui.theme.MicroSproutsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize the Database and Repository
        val database = MicroSproutsDatabase.getDatabase(applicationContext)
        val repository = TaskRepository(database.taskDao())

        setContent {
            MicroSproutsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Create the ViewModel and pass it to the HomeScreen
                    val homeViewModel: HomeViewModel = viewModel(
                        factory = HomeViewModelFactory(repository)
                    )

                    HomeScreen(viewModel = homeViewModel)
                }
            }
        }
    }
}