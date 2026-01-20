package dev.vicart.pixelcount.data.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import java.nio.file.Path
import java.util.Properties
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.createParentDirectories
import kotlin.io.path.exists

actual class DriverFactory {

    private val userHomePath = Path(System.getProperty("user.home"))
    private val dbPath: Path = System.getProperty("os.name").lowercase().let {
        when {
            it.startsWith("linux") -> Path(userHomePath.absolutePathString(),
                ".local", "share", "pixelcount")
            else -> throw RuntimeException("Host not supported")
        }
    }

    private val dbFile = Path(dbPath.absolutePathString(), "pixelcount.db")

    init {
        if(!dbPath.exists()) {
            dbPath.createParentDirectories()
        }
    }

    actual fun createDriver(): SqlDriver {
        val driver = JdbcSqliteDriver("jdbc:sqlite:${dbFile.absolutePathString()}", Properties().apply {
            put("foreign_keys", "true")
        }, PixelCountDatabase.Schema)
        return driver
    }
}