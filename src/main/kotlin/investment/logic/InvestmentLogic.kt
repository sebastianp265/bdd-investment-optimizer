package com.github.sebastianp265.investment.logic

import com.github.sebastianp265.graph.Transition
import com.github.sebastianp265.investment.common.Money
import com.github.sebastianp265.investment.common.Month
import com.github.sebastianp265.investment.state.InvestmentSimulationState

object InvestmentLogic {

    fun createTransition(
        decisions: List<InvestmentDecision>,
        state: InvestmentSimulationState,
        monthlyDeposit: Money,
    ): Transition<InvestmentSimulationState, InvestmentDecision> {
        val stateAfterDecisions = decisions.fold(state) { currentState, decision ->
            decision.apply(currentState)
        }
        return Transition(decisions, advanceMonth(stateAfterDecisions, monthlyDeposit))
    }

    private fun advanceMonth(
        state: InvestmentSimulationState,
        monthlyDeposit: Money,
    ): InvestmentSimulationState {
        val monthlyUpdates = state.investments.map {
            it.advanceMonth(
                currentMonth = state.currentMonth,
                promotionStartMonths = state.promotionStartMonths,
            )
        }
        val maturedCash = monthlyUpdates.fold(Money.ZERO) { acc, update -> acc + update.maturedCash }
        val updatedInvestments = monthlyUpdates.mapNotNull { it.updatedInvestment }

        val nextMonth = state.currentMonth + Month.ONE

        return state.copy(
            currentMonth = nextMonth,
            availableCash = state.availableCash + monthlyDeposit + maturedCash,
            investments = updatedInvestments,
        )
    }

}
