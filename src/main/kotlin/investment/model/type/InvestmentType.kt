package com.github.sebastianp265.investment.model.type

import com.github.sebastianp265.investment.common.Rate

sealed class InvestmentType {
    abstract val rate: Rate
}
