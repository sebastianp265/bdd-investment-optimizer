package com.github.sebastianp265.investment

import com.github.sebastianp265.investment.common.Money

sealed class Investment {
    abstract val principal: Money

    fun currentValue(): Money = principal
}
