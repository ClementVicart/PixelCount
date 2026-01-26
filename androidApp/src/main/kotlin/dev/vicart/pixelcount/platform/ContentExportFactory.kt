package dev.vicart.pixelcount.platform

import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.coroutines.CompletableDeferred
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import java.io.ByteArrayOutputStream

fun ComponentActivity.registerContentExportFactory() {
    contentExportFactory = ContentExportFactoryImpl(this)
}

private class ContentExportFactoryImpl(private val activity: ComponentActivity) : ContentExportFactory {

    private lateinit var dataToExport: ByteArray

    private var openedFileDeferred: CompletableDeferred<Uri?>? = null

    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val saveFileLauncher = activity.registerForActivityResult(ActivityResultContracts.CreateDocument("application/json")) {
        if(it != null) {
            activity.contentResolver.openOutputStream(it)?.use {
                it.write(dataToExport)
            }

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/json"
                putExtra(Intent.EXTRA_STREAM, it)
            }

            activity.startActivity(Intent.createChooser(intent, null))
        }
    }

    private val openFileLauncher = activity.registerForActivityResult(ActivityResultContracts.OpenDocument()) {
        openedFileDeferred?.complete(it)
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun <T> export(title: String, data: T, serializer: KSerializer<T>) {
        dataToExport = ByteArrayOutputStream().apply {
            json.encodeToStream(serializer, data, this)
        }.toByteArray()
        saveFileLauncher.launch("$title.json")
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun <T> import(serialier: KSerializer<T>): T? {
        openedFileDeferred?.cancel()
        openedFileDeferred = CompletableDeferred()
        openFileLauncher.launch(arrayOf("application/json"))
        val uri = openedFileDeferred?.await()
        return uri?.let {
            activity.contentResolver.openInputStream(it)?.use {
                json.decodeFromStream(serialier, it)
            }
        }
    }

}