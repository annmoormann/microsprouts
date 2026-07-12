package com.example.microsprouts.ui.home

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun HomeScreen(
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToAdd: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    Text("MicroSprouts Home Screen Placeholder")
}
