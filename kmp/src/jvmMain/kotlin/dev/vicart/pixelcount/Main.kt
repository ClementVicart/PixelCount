package dev.vicart.pixelcount

import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.window.singleWindowApplication
import dev.vicart.pixelcount.data.database.Database
import dev.vicart.pixelcount.data.database.DriverFactory
import dev.vicart.pixelcount.data.database.createDatabase
import dev.vicart.pixelcount.resources.Res
import dev.vicart.pixelcount.resources.favicon
import dev.vicart.pixelcount.ui.App
import org.jetbrains.compose.resources.decodeToImageBitmap
import org.jetbrains.compose.resources.getDrawableResourceBytes
import org.jetbrains.compose.resources.getSystemResourceEnvironment

suspend fun main() {
    Database.init(createDatabase(DriverFactory()))

    singleWindowApplication(
        title = "PixelCount",
        icon = getDrawableResourceBytes(getSystemResourceEnvironment(),
            Res.drawable.favicon).let {
                BitmapPainter(it.decodeToImageBitmap())
        }
    ) {
        App()
    }
}