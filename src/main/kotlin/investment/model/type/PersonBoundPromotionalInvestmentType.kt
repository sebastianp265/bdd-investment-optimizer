package com.github.sebastianp265.investment.model.type

import com.github.sebastianp265.investment.common.Month
import com.github.sebastianp265.investment.common.Rate

data class PersonBoundPromotionalInvestmentType(
    override val rate: Rate,
    val promotionalRate: Rate,
    val promotionDurationMonths: Month,
) : InvestmentType()
