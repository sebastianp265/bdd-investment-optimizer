package com.github.sebastianp265.investment.model

import com.github.sebastianp265.investment.common.Money
import com.github.sebastianp265.investment.common.Month
import com.github.sebastianp265.investment.model.type.InvestmentType

data class InvestmentMonthlyAdvanceResult(
    val maturedCash: Money = Money.ZERO,
    val updatedInvestment: Investment?,
)

sealed class Investment {
    abstract val principal: Money

    open fun liquidationValue(): Money = principal

    abstract fun advanceMonth(
        currentMonth: Month,
        promotionStartMonths: Map<InvestmentType, Month>,
    ): InvestmentMonthlyAdvanceResult
}
