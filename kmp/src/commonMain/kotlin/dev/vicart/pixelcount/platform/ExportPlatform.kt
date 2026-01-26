package dev.vicart.pixelcount.platform

import kotlinx.serialization.KSerializer

expect fun <T> export(title: String, data: T, serializer: KSerializer<T>)

expect suspend fun <T> import(serializer: KSerializer<T>) : T?