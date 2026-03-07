package com.github.sebastianp265.investment.simulation

import com.github.sebastianp265.investment.common.Money
import com.github.sebastianp265.investment.logic.InvestmentDecision
import com.github.sebastianp265.investment.logic.InvestmentLogic
import com.github.sebastianp265.investment.state.InvestmentSimulationState

object InvestmentSimulationRunner {

    fun replay(
        initialState: InvestmentSimulationState,
        decisions: List<List<InvestmentDecision>>,
        monthlyDeposit: Money = Money.ZERO,
    ): InvestmentSimulationState {
        return decisions.fold(initialState) { state, monthDecisions ->
            InvestmentLogic.createTransition(monthDecisions, state, monthlyDeposit).nextState
        }
    }

}

