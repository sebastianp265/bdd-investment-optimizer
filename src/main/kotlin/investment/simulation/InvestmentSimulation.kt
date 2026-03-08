package com.github.sebastianp265.investment.simulation

import com.github.sebastianp265.graph.StateGraph
import com.github.sebastianp265.graph.Transition
import com.github.sebastianp265.investment.common.Money
import com.github.sebastianp265.investment.common.Month
import com.github.sebastianp265.investment.logic.InvestmentDecision
import com.github.sebastianp265.investment.logic.InvestmentLogic
import com.github.sebastianp265.investment.model.type.InvestmentType
import com.github.sebastianp265.investment.state.InvestmentSimulationState

class InvestmentSimulation(
    private val finalMonth: Month,
    private val availableInvestmentTypes: List<InvestmentType>,
    private val monthlyDeposit: Money = Money.ZERO,
) : StateGraph<InvestmentSimulationState, InvestmentDecision> {

    override fun possibleTransitions(state: InvestmentSimulationState): List<Transition<InvestmentSimulationState, InvestmentDecision>> {
        if (state.currentMonth >= finalMonth) {
            return emptyList()
        }

        return buildList {
            add(createDoNothingTransition(state))
            addAll(createInvestTransitions(state))
            addAll(createWithdrawTransitions(state))
        }
    }

    private fun createDoNothingTransition(state: InvestmentSimulationState): Transition<InvestmentSimulationState, InvestmentDecision> {
        return InvestmentLogic.createTransition(
            listOf(InvestmentDecision.DoNothing),
            state,
            monthlyDeposit,
        )
    }

    private fun createInvestTransitions(state: InvestmentSimulationState): List<Transition<InvestmentSimulationState, InvestmentDecision>> {
        if (state.availableCash <= Money.ZERO) {
            return emptyList()
        }

        return availableInvestmentTypes.map { account ->
            val decision = InvestmentDecision.InvestAll(account)
            InvestmentLogic.createTransition(
                listOf(decision),
                state,
                monthlyDeposit,
            )
        }
    }

    private fun createWithdrawTransitions(state: InvestmentSimulationState): List<Transition<InvestmentSimulationState, InvestmentDecision>> {
        if (state.investments.isEmpty()) {
            return emptyList()
        }

        return buildList {
            add(
                InvestmentLogic.createTransition(
                    listOf(InvestmentDecision.Withdraw),
                    state,
                    monthlyDeposit,
                )
            )

            if (state.totalLiquidationValue() > Money.ZERO) {
                availableInvestmentTypes.forEach { account ->
                    val investAllDecision = InvestmentDecision.InvestAll(account)
                    add(
                        InvestmentLogic.createTransition(
                            listOf(InvestmentDecision.Withdraw, investAllDecision),
                            state,
                            monthlyDeposit,
                        )
                    )
                }
            }
        }
    }
}

