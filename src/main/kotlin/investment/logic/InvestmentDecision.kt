package com.github.sebastianp265.investment.logic

import com.github.sebastianp265.investment.model.type.InvestmentType

sealed class InvestmentDecision {
    data class InvestAll(val investmentType: InvestmentType) : InvestmentDecision()
    object Withdraw : InvestmentDecision()
    object DoNothing : InvestmentDecision()
}