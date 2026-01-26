package dev.vicart.pixelcount.platform

import androidx.activity.ComponentActivity
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

fun ComponentActivity.registerContentProviderFactory() {
    contentProviderFactory = ContentProviderFactoryImpl(this)
}

private class ContentProviderFactoryImpl(private val activity: ComponentActivity) : ContentProviderFactory {

    private var imageDeferred: CompletableDeferred<ByteArray?>? = null

    private val pickImageLauncher = activity.registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        imageDeferred?.complete(it?.let {
            activity.contentResolver.openInputStream(it)?.use {
                it.readBytes()
            }
        })
    }

    override suspend fun pickImage(): ByteArray? {
        imageDeferred?.cancel()
        imageDeferred = CompletableDeferred()
        pickImageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        return imageDeferred?.await()
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun readImage(id: Uuid): ByteArray? = withContext(Dispatchers.IO) {
        File(activity.cacheDir, id.toString()).takeIf { it.exists() }?.readBytes()
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun writeImage(id: Uuid, data: ByteArray) = withContext(Dispatchers.IO) {
        File(activity.cacheDir, id.toString()).writeBytes(data)
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun deleteImage(id: Uuid) = withContext(Dispatchers.IO) {
        File(activity.cacheDir, id.toString()).takeIf { it.exists() }?.delete()
        Unit
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun hasImage(id: Uuid): Boolean = withContext(Dispatchers.IO) {
        File(activity.cacheDir, id.toString()).exists()
    }

}