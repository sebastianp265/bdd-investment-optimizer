package com.github.sebastianp265.investment.common

import java.math.BigDecimal
import java.math.RoundingMode

fun BigDecimal.toRate() = Rate(this)

@JvmInline
value class Rate(val value: BigDecimal) {

    init {
        require(value > BigDecimal.ZERO)
        value.setScale(12, RoundingMode.HALF_UP)
    }

    operator fun div(divisor: Int) = Rate(value.divide(divisor.toBigDecimal(), 12, RoundingMode.HALF_UP))
    operator fun plus(other: Rate) = Rate(value + other.value)

}