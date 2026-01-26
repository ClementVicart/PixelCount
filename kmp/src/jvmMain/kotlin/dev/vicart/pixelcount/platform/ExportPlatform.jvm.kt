package dev.vicart.pixelcount.platform

import dev.vicart.pixelcount.topWindow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

private val json = Json {
    ignoreUnknownKeys = true
}

@OptIn(ExperimentalSerializationApi::class)
actual fun <T> export(
    title: String,
    data: T,
    serializer: KSerializer<T>
) {
    val chooser = JFileChooser()
    val filter = FileNameExtensionFilter("JSON files", "json")
    chooser.fileFilter = filter
    val retVal = chooser.showSaveDialog(topWindow)
    if(retVal == JFileChooser.APPROVE_OPTION) {
        chooser.selectedFile.outputStream().use {
            json.encodeToStream(serializer, data, it)
        }
    }
}

@OptIn(ExperimentalSerializationApi::class)
actual suspend fun <T> import(serializer: KSerializer<T>): T? {
    val chooser = JFileChooser()
    val filter = FileNameExtensionFilter("JSON files", "json")
    chooser.fileFilter = filter
    val retVal = chooser.showOpenDialog(topWindow)
    if(retVal == JFileChooser.APPROVE_OPTION) {
        return withContext(Dispatchers.IO) {
            chooser.selectedFile.inputStream().use {
                json.decodeFromStream(serializer, it)
            }
        }
    }
    return null
}