package com.github.sebastianp265.investment

import com.github.sebastianp265.investment.common.Money
import com.github.sebastianp265.investment.common.Month
import com.github.sebastianp265.investment.common.Rate

data class FixedRateInvestment(
    val principal: Money,
    val investmentMonth: Month,
    val rate: Rate
) : Investment() {

    override fun currentValue(): Money {
        return principal
    }
}

