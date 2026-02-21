package com.github.sebastianp265.domain.value

import java.math.BigDecimal

@JvmInline
value class Rate(val value: BigDecimal) {

    init {
        require(value > BigDecimal.ZERO)
    }

}