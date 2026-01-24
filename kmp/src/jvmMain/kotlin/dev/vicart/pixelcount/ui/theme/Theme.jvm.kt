package dev.vicart.pixelcount.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun getColorScheme(systemDark: Boolean): ColorScheme {
    return remember(systemDark) {
        if(systemDark) darkScheme else lightScheme
    }
}