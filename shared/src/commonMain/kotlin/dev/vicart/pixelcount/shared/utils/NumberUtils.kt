package dev.vicart.pixelcount.shared.utils

import java.math.RoundingMode
import java.util.Currency

val Double.prettyPrint: String
    get() = this.toBigDecimal().setScale(2, RoundingMode.HALF_DOWN).toString()

fun Double.prettyPrint(currency: Currency) : String = "$prettyPrint ${currency.symbol}"