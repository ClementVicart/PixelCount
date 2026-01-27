package dev.vicart.pixelcount.shared.model

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class Participant(
    val id: Uuid = Uuid.random(),
    val name: String,
    val mandatory: Boolean = false
)
