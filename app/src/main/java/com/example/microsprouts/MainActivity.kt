package com.example.microsprouts

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.rememberNavBackStack
import com.example.microsprouts.ui.navigation.Destination
import com.example.microsprouts.ui.navigation.MicroSproutsNavHost
import com.example.microsprouts.ui.theme.MicroSproutsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MicroSproutsTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    @Suppress("UNCHECKED_CAST")
                    val backStack = rememberNavBackStack(Destination.Home) as NavBackStack<Destination>
                    MicroSproutsNavHost(backStack = backStack)
                }
            }
        }
    }
}
