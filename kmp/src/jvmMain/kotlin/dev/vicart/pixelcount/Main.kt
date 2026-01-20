package dev.vicart.pixelcount

import androidx.compose.ui.window.singleWindowApplication
import dev.vicart.pixelcount.data.database.Database
import dev.vicart.pixelcount.data.database.DriverFactory
import dev.vicart.pixelcount.data.database.createDatabase
import dev.vicart.pixelcount.ui.App

fun main() {
    Database.init(createDatabase(DriverFactory()))

    singleWindowApplication(
        title = "PixelCount"
    ) {
        App()
    }
}