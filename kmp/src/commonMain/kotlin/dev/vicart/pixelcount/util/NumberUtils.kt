package dev.vicart.pixelcount.util

import java.math.RoundingMode

val Double.prettyPrint: String
    get() = this.toBigDecimal().setScale(2, RoundingMode.HALF_DOWN).toString()