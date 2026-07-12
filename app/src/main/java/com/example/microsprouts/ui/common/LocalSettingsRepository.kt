package com.example.microsprouts.ui.common

import androidx.compose.runtime.staticCompositionLocalOf
import com.example.microsprouts.data.settings.SettingsRepository

val LocalSettingsRepository = staticCompositionLocalOf<SettingsRepository> {
    error("No SettingsRepository provided")
}
