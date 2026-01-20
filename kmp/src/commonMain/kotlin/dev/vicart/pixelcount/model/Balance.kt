package dev.vicart.pixelcount.model

data class Balance(
    val from: Participant,
    val to: Participant,
    val amount: Double
)
