package dev.vicart.pixelcount.model

import kotlinx.serialization.Serializable
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Serializable
data class Expense(
    val id: Uuid = Uuid.random(),
    val type: PaymentTypeEnum,
    val label: String,
    val amount: Double,
    val paidBy: Participant,
    val sharedWith: List<Participant>,
    val datetime: Instant
)
