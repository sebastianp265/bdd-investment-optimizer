package com.github.sebastianp265.investment.model

import com.github.sebastianp265.investment.common.Money
import com.github.sebastianp265.investment.common.Month
import com.github.sebastianp265.investment.common.Rate

data class FixedRateInvestment(
    override val principal: Money,
    val investmentMonth: Month,
    val rate: Rate,
) : Investment()

