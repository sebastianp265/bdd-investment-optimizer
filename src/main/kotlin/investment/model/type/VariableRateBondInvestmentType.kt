package com.github.sebastianp265.investment.model.type

import com.github.sebastianp265.investment.common.Month
import com.github.sebastianp265.investment.common.Rate

data class VariableRateBondInvestmentType(
    val firstPeriodRate: Rate,
    val baseRate: Rate,
    val margin: Rate,
    val durationMonths: Month,
    val earlyRedemptionPenaltyRate: Rate,
) : InvestmentType() {
    override val rate: Rate get() = baseRate
}
