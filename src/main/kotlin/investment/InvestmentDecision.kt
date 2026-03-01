package com.github.sebastianp265.investment

import com.github.sebastianp265.investment.common.Money

sealed class InvestmentDecision {
    data class Invest(val investment: Investment, val amount: Money) : InvestmentDecision()
    object Withdraw : InvestmentDecision()
    object DoNothing : InvestmentDecision()
}

