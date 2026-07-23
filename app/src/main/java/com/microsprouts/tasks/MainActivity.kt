package com.microsprouts.tasks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.microsprouts.tasks.data.database.MicroSproutsDatabase
import com.microsprouts.tasks.data.repository.TaskRepository
import com.microsprouts.tasks.data.worker.MissedBehaviorWorker
import com.microsprouts.tasks.ui.home.HomeScreen
import com.microsprouts.tasks.ui.home.HomeViewModel
import com.microsprouts.tasks.ui.home.HomeViewModelFactory
import com.microsprouts.tasks.ui.theme.MicroSproutsTheme
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Schedule the background WorkManager task rollover engine
        setupDailyRolloverWorker()

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

    private fun setupDailyRolloverWorker() {
        val rolloverWorkRequest = PeriodicWorkRequestBuilder<MissedBehaviorWorker>(
            24, TimeUnit.HOURS
        ).build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "MicroSproutsDailyRollover",
            ExistingPeriodicWorkPolicy.KEEP,
            rolloverWorkRequest
        )
    }
}