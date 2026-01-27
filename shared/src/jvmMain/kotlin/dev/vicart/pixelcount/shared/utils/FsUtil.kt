package dev.vicart.pixelcount.shared.utils

import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.notExists

val cacheDirectory: Path = System.getProperty("os.name").lowercase().let {
    val userHome = System.getProperty("user.home")
    when {
        it.startsWith("linux") -> Path(userHome, ".cache", "pixelcount")
        it.startsWith("windows") -> Path(userHome, "AppData", "Local", "PixelCount")
        else -> throw RuntimeException("Host not supported")
    }
}.also {
    if(it.notExists()) {
        it.createDirectories()
    }
}