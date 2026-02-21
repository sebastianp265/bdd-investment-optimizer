package com.github.sebastianp265.domain.value

import java.math.BigDecimal
import java.math.RoundingMode

@JvmInline
value class Money(val value: BigDecimal) : Comparable<Money> {

    companion object {
        private val ROUNDING = RoundingMode.HALF_UP
        val ZERO = Money(BigDecimal.ZERO)
    }

    init {
        require(value.scale() <= 2)
        require(value >= BigDecimal.ZERO)
    }

    operator fun plus(other: Money) = Money(value + other.value)
    operator fun minus(other: Money) = Money(value - other.value)
    operator fun times(factor: BigDecimal) = Money((value * factor).setScale(2, ROUNDING))
    operator fun times(factor: Rate) = Money(value * factor.value)
    operator fun div(divisor: BigDecimal): Money {
        require(divisor != BigDecimal.ZERO)
        return Money((value / divisor).setScale(2, ROUNDING))
    }

    override operator fun compareTo(other: Money): Int = value.compareTo(other.value)

    override fun toString(): String = "$$value"
}