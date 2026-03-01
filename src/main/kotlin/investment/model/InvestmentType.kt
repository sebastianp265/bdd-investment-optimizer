package com.github.sebastianp265.investment.model

import com.github.sebastianp265.investment.common.Month
import com.github.sebastianp265.investment.common.Rate

sealed class InvestmentType {
    abstract val rate: Rate
}

data class FixedRateType(
    override val rate: Rate,
) : InvestmentType()

data class PersonBoundPromotionalType(
    override val rate: Rate,
    val promotionalRate: Rate,
    val promotionDurationMonths: Month,
) : InvestmentType()

