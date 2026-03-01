package com.github.sebastianp265.investment.common

import java.math.BigDecimal
import java.math.RoundingMode

@JvmInline
value class Rate(val value: BigDecimal) {

    init {
        require(value > BigDecimal.ZERO)
    }

    operator fun div(divisor: Int) = Rate(value.divide(divisor.toBigDecimal(), 12, RoundingMode.HALF_UP))

}