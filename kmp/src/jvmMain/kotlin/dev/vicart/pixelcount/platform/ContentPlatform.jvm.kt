package dev.vicart.pixelcount.platform

import dev.vicart.pixelcount.topWindow
import dev.vicart.pixelcount.util.cacheDirectory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter
import kotlin.io.path.absolutePathString
import kotlin.uuid.Uuid

actual suspend fun pickImage(): ByteArray? {
    val chooser = JFileChooser()
    val filter = FileNameExtensionFilter("Image files", "png", "jpg", "jpeg")
    chooser.fileFilter = filter
    chooser.fileSelectionMode = JFileChooser.FILES_ONLY
    chooser.isMultiSelectionEnabled = false

    val returnVal = chooser.showOpenDialog(topWindow)
    if(returnVal == JFileChooser.APPROVE_OPTION) {
        return withContext(Dispatchers.IO) {
            chooser.selectedFile.readBytes()
        }
    }
    return null
}

actual suspend fun readImage(id: Uuid): ByteArray? = withContext(Dispatchers.IO) {
    File(cacheDirectory.absolutePathString(), id.toString()).takeIf { it.exists() }?.readBytes()
}

actual suspend fun writeImage(id: Uuid, data: ByteArray) = withContext(Dispatchers.IO) {
    File(cacheDirectory.absolutePathString(), id.toString()).writeBytes(data)
}

actual suspend fun deleteImage(id: Uuid) = withContext(Dispatchers.IO) {
    File(cacheDirectory.absolutePathString(), id.toString()).takeIf { it.exists() }?.delete()
    Unit
}

actual suspend fun hasImage(id: Uuid): Boolean = withContext(Dispatchers.IO) {
    File(cacheDirectory.absolutePathString(), id.toString()).exists()
}