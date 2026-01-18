package dev.vicart.pixelcount.model

import kotlinx.serialization.Serializable

@Serializable
data class Emoji(
    val emoji: String,
    val annotation: String,
    val group: String,
    val tags: String
)
