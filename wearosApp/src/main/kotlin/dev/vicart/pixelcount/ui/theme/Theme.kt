package dev.vicart.pixelcount.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.wear.compose.material3.ColorScheme
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.MotionScheme
import androidx.wear.compose.material3.dynamicColorScheme

@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val scheme = remember(context) {
        dynamicColorScheme(context) ?: colorScheme
    }

    MaterialTheme(
        colorScheme = scheme,
        motionScheme = MotionScheme.expressive(),
        typography = typography,
        content = content
    )
}