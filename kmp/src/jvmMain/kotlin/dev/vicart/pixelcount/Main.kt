package dev.vicart.pixelcount

import androidx.compose.ui.window.singleWindowApplication
import dev.vicart.pixelcount.ui.App

fun main() = singleWindowApplication(
    title = "PixelCount"
) {
    App()
}