package com.github.sebastianp265.investment

import com.github.sebastianp265.investment.common.Money
import com.github.sebastianp265.investment.common.Month

data class InvestmentSimulationState(
    val currentMonth: Month,
    val availableCash: Money,
    val investments: List<Investment>,
    val promotionStartMonths: Map<InvestmentType, Month> = emptyMap(),
) : Comparable<InvestmentSimulationState> {

    override fun compareTo(other: InvestmentSimulationState): Int {
        return totalValue().compareTo(other.totalValue())
    }

    fun totalValue(): Money = availableCash + investments.fold(Money.ZERO) { acc, inv -> acc + inv.currentValue() }
}

