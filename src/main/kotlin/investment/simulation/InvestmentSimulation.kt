package com.github.sebastianp265.investment.simulation

import com.github.sebastianp265.graph.StateGraph
import com.github.sebastianp265.graph.Transition
import com.github.sebastianp265.investment.common.Money
import com.github.sebastianp265.investment.common.Month
import com.github.sebastianp265.investment.logic.InvestmentLogic
import com.github.sebastianp265.investment.model.InvestmentDecision
import com.github.sebastianp265.investment.model.InvestmentType
import com.github.sebastianp265.investment.state.InvestmentSimulationState

class InvestmentSimulation(
    private val finalMonth: Month,
    private val availableAccounts: List<InvestmentType>,
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

        return availableAccounts.map { account ->
            val decision = InvestmentDecision.Invest(account, state.availableCash)
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

            val stateAfterWithdraw = InvestmentLogic.applyWithdrawDecision(state)
            if (stateAfterWithdraw.availableCash > Money.ZERO) {
                availableAccounts.forEach { account ->
                    val investDecision = InvestmentDecision.Invest(account, stateAfterWithdraw.availableCash)
                    add(
                        InvestmentLogic.createTransition(
                            listOf(InvestmentDecision.Withdraw, investDecision),
                            state,
                            monthlyDeposit,
                        )
                    )
                }
            }
        }
    }
}

