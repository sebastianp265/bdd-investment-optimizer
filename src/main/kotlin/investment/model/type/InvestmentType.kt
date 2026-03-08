package com.github.sebastianp265.investment.model.type

import com.github.sebastianp265.investment.common.Money
import com.github.sebastianp265.investment.common.Month
import com.github.sebastianp265.investment.model.Investment

data class InvestmentCreationResult(
    val investment: Investment,
    val remainingCash: Money,
)

sealed interface InvestmentType {
    fun createInvestment(
        cashToSpend: Money,
        currentMonth: Month,
    ): InvestmentCreationResult
}
