package com.github.sebastianp265.investment.model.type

import com.github.sebastianp265.investment.common.Money
import com.github.sebastianp265.investment.common.Month
import com.github.sebastianp265.investment.common.Rate
import com.github.sebastianp265.investment.model.PersonBoundPromotionalInvestment

data class PersonBoundPromotionalInvestmentType(
    val rate: Rate,
    val promotionalRate: Rate,
    val promotionDurationMonths: Month,
) : InvestmentType {

    override fun createInvestment(
        cashToSpend: Money,
        currentMonth: Month,
    ): InvestmentCreationResult {
        return InvestmentCreationResult(
            investment = PersonBoundPromotionalInvestment(
                principal = cashToSpend,
                investmentMonth = currentMonth,
                type = this,
            ),
            remainingCash = Money.ZERO,
        )
    }
}
