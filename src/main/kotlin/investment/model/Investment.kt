package com.github.sebastianp265.investment.model

import com.github.sebastianp265.investment.common.Money

sealed class Investment {
    abstract val principal: Money
    
    fun currentValue(): Money = principal
}

