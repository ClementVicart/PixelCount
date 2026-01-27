package dev.vicart.pixelcount.shared.model

data class Balance(
    val from: Participant,
    val to: Participant,
    val amount: Double
)
