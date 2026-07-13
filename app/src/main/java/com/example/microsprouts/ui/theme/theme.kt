package com.example.microsprouts.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val MicroSproutsColorScheme = lightColorScheme(
    primary = SagePrimary,
    onPrimary = SoftWhite,
    primaryContainer = SageContainer,
    onPrimaryContainer = SlateText,
    secondary = TerracottaSecondary,
    background = WarmSand,
    surface = SoftWhite,
    onBackground = SlateText,
    onSurface = SlateText
)

@Composable
fun MicroSproutsTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MicroSproutsColorScheme,
        content = content
    )
}