package com.github.sebastianp265.investment.model.type

import com.github.sebastianp265.investment.common.Money
import com.github.sebastianp265.investment.common.Month
import com.github.sebastianp265.investment.common.Rate
import com.github.sebastianp265.investment.model.FixedRateInvestment

data class FixedRateInvestmentType(
    val rate: Rate,
) : InvestmentType {

    override fun createInvestment(
        cashToSpend: Money,
        currentMonth: Month,
    ): InvestmentCreationResult {
        return InvestmentCreationResult(
            investment = FixedRateInvestment(
                principal = cashToSpend,
                investmentMonth = currentMonth,
                rate = rate,
            ),
            remainingCash = Money.ZERO,
        )
    }
}
