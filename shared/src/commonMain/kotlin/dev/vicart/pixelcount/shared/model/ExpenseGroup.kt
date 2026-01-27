package dev.vicart.pixelcount.shared.model

import dev.vicart.pixelcount.shared.model.serializer.CurrencySerializer
import kotlinx.serialization.Serializable
import java.util.Currency
import kotlin.uuid.Uuid

@Serializable
data class ExpenseGroup(
    val id: Uuid = Uuid.random(),
    val emoji: String,
    val title: String,
    val participants: List<Participant>,
    val expenses: List<Expense>,
    @Serializable(with = CurrencySerializer::class)
    val currency: Currency
)
