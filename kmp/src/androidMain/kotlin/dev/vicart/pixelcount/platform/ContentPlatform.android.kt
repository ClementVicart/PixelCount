package dev.vicart.pixelcount.platform

import kotlin.uuid.Uuid

interface ContentProviderFactory {

    suspend fun pickImage() : ByteArray?

    suspend fun readImage(id: Uuid) : ByteArray?

    suspend fun writeImage(id: Uuid, data: ByteArray)

    suspend fun deleteImage(id: Uuid)

    suspend fun hasImage(id: Uuid) : Boolean
}

lateinit var contentProviderFactory: ContentProviderFactory

actual suspend fun pickImage(): ByteArray? = contentProviderFactory.pickImage()
actual suspend fun readImage(id: Uuid): ByteArray? = contentProviderFactory.readImage(id)
actual suspend fun writeImage(id: Uuid, data: ByteArray) = contentProviderFactory.writeImage(id, data)

actual suspend fun deleteImage(id: Uuid) = contentProviderFactory.deleteImage(id)

actual suspend fun hasImage(id: Uuid): Boolean = contentProviderFactory.hasImage(id)