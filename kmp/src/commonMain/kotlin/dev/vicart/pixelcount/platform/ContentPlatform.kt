package dev.vicart.pixelcount.platform

import kotlin.uuid.Uuid

expect suspend fun pickImage() : ByteArray?

expect suspend fun readImage(id: Uuid) : ByteArray?

expect suspend fun writeImage(id: Uuid, data: ByteArray)

expect suspend fun deleteImage(id: Uuid)

expect suspend fun hasImage(id: Uuid) : Boolean

expect suspend fun readQrCode() : String?

expect val canScanQrCode: Boolean