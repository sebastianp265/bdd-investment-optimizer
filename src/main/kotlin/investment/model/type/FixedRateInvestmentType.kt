package com.github.sebastianp265.investment.model.type

import com.github.sebastianp265.investment.common.Rate

data class FixedRateInvestmentType(
    override val rate: Rate,
) : InvestmentType()
