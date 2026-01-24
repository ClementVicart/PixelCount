package dev.vicart.pixelcount.ui.theme

import android.os.Build
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun getColorScheme(systemDark: Boolean): ColorScheme {
    val context = LocalContext.current

    return remember(context, systemDark) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if(systemDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        } else {
            if(systemDark) darkScheme else lightScheme
        }
    }
}