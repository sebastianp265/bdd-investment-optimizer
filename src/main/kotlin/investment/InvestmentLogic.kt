package com.github.sebastianp265.investment

import com.github.sebastianp265.graph.Transition
import com.github.sebastianp265.investment.common.Money
import com.github.sebastianp265.investment.common.Month
import java.math.BigDecimal

object InvestmentLogic {

    fun createTransition(
        decisions: List<InvestmentDecision>,
        state: InvestmentSimulationState,
        monthlyDeposit: Money
    ): Transition<InvestmentSimulationState, InvestmentDecision> {
        val stateAfterDecisions = decisions.fold(state) { currentState, decision ->
            when (decision) {
                is InvestmentDecision.Invest -> applyInvestDecision(currentState, decision)
                is InvestmentDecision.Withdraw -> applyWithdrawDecision(currentState)
                is InvestmentDecision.DoNothing -> currentState
            }
        }
        return Transition(decisions, advanceMonth(stateAfterDecisions, monthlyDeposit))
    }

    private fun advanceMonth(
        state: InvestmentSimulationState,
        monthlyDeposit: Money
    ): InvestmentSimulationState {
        val nextMonth = state.currentMonth + Month.ONE

        val updatedInvestments = state.investments.map { investment ->
            when(investment) {
                is FixedRateInvestment -> {
                    val newPrincipal = investment.principal * (BigDecimal.ONE + (investment.rate / 12).value)
                    investment.copy(principal = newPrincipal)
                }
            }
        }

        return state.copy(
            currentMonth = nextMonth,
            availableCash = state.availableCash + monthlyDeposit,
            investments = updatedInvestments
        )
    }

    private fun applyInvestDecision(
        state: InvestmentSimulationState,
        decision: InvestmentDecision.Invest
    ): InvestmentSimulationState {
        require(decision.amount <= state.availableCash) {
            "Cannot invest ${decision.amount} when only ${state.availableCash} is available"
        }

        val newInvestment = when (val template = decision.investment) {
            is FixedRateInvestment -> FixedRateInvestment(
                principal = decision.amount,
                investmentMonth = state.currentMonth,
                rate = template.rate
            )
        }

        return state.copy(
            availableCash = state.availableCash - decision.amount,
            investments = state.investments + newInvestment
        )
    }

    fun applyWithdrawDecision(state: InvestmentSimulationState): InvestmentSimulationState {
        val withdrawnAmount = state.investments.fold(Money.ZERO) { acc, inv -> acc + inv.currentValue() }
        return state.copy(
            availableCash = state.availableCash + withdrawnAmount,
            investments = emptyList()
        )
    }
}





