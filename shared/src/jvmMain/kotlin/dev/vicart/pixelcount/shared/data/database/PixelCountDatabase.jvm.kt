package dev.vicart.pixelcount.shared.data.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import dev.vicart.pixelcount.shared.utils.cacheDirectory
import java.util.Properties
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString

actual class DriverFactory {

    private val dbFile = Path(cacheDirectory.absolutePathString(), "pixelcount.db")

    actual fun createDriver(): SqlDriver {
        val driver = JdbcSqliteDriver("jdbc:sqlite:${dbFile.absolutePathString()}", Properties().apply {
            put("foreign_keys", "true")
        }, PixelCountDatabase.Schema)
        return driver
    }
}