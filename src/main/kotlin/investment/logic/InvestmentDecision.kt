package com.github.sebastianp265.investment.logic

import com.github.sebastianp265.investment.model.type.InvestmentType
import com.github.sebastianp265.investment.state.InvestmentSimulationState

sealed class InvestmentDecision {
    abstract fun apply(state: InvestmentSimulationState): InvestmentSimulationState

    data class InvestAll(val investmentType: InvestmentType) : InvestmentDecision() {
        override fun apply(state: InvestmentSimulationState): InvestmentSimulationState {
            val created = investmentType.createInvestment(
                cashToSpend = state.availableCash,
                currentMonth = state.currentMonth,
            )
            val updatedPromotionStartMonths = PromotionTrackingPolicy.updateAfterInvestAll(
                current = state.promotionStartMonths,
                investmentType = investmentType,
                currentMonth = state.currentMonth,
            )

            return state.copy(
                availableCash = created.remainingCash,
                investments = state.investments + created.investment,
                promotionStartMonths = updatedPromotionStartMonths,
            )
        }
    }

    object Withdraw : InvestmentDecision() {
        override fun apply(state: InvestmentSimulationState): InvestmentSimulationState {
            return state.copy(
                availableCash = state.totalLiquidationValue(),
                investments = emptyList(),
            )
        }
    }

    object DoNothing : InvestmentDecision() {
        override fun apply(state: InvestmentSimulationState): InvestmentSimulationState = state
    }
}