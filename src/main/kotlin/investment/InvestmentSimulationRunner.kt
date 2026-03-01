package com.github.sebastianp265.investment

import com.github.sebastianp265.investment.common.Money

object InvestmentSimulationRunner {

    fun replay(
        initialState: InvestmentSimulationState,
        decisions: List<List<InvestmentDecision>>,
        monthlyDeposit: Money = Money.ZERO
    ): InvestmentSimulationState {
        return decisions.fold(initialState) { state, monthDecisions ->
            InvestmentLogic.createTransition(monthDecisions, state, monthlyDeposit).nextState
        }
    }

}



