package dev.vicart.pixelcount.platform

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer

interface ContentExportFactory {

    fun <T> export(title: String, data: T, serializer: KSerializer<T>)

    suspend fun <T> import(serialier: KSerializer<T>) : T?
}

lateinit var contentExportFactory: ContentExportFactory

@OptIn(InternalSerializationApi::class)
actual fun <T> export(title: String, data: T, serializer: KSerializer<T>) = contentExportFactory.export(title, data, serializer)

actual suspend fun <T> import(serializer: KSerializer<T>): T? = contentExportFactory.import(serializer)