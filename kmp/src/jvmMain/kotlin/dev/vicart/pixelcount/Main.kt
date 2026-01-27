package dev.vicart.pixelcount

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import dev.vicart.pixelcount.resources.Res
import dev.vicart.pixelcount.resources.favicon
import dev.vicart.pixelcount.shared.data.database.Database
import dev.vicart.pixelcount.shared.data.database.DriverFactory
import dev.vicart.pixelcount.shared.data.database.createDatabase
import dev.vicart.pixelcount.ui.App
import org.jetbrains.compose.resources.painterResource
import java.awt.Window

lateinit var topWindow: Window

fun main() {
    Database.init(createDatabase(DriverFactory()))

    application {

        Window(
            onCloseRequest = ::exitApplication,
            title = "",
            icon = painterResource(Res.drawable.favicon)
        ) {
            LaunchedEffect(Unit) {
                topWindow = window
            }
            App()
        }
    }
}