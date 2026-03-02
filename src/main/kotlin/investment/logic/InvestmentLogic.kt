package com.github.sebastianp265.investment.logic

import com.github.sebastianp265.graph.Transition
import com.github.sebastianp265.investment.common.Money
import com.github.sebastianp265.investment.common.Month
import com.github.sebastianp265.investment.model.*
import com.github.sebastianp265.investment.state.InvestmentSimulationState
import java.math.BigDecimal

object InvestmentLogic {

    fun createTransition(
        decisions: List<InvestmentDecision>,
        state: InvestmentSimulationState,
        monthlyDeposit: Money,
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
        monthlyDeposit: Money,
    ): InvestmentSimulationState {
        val updatedInvestments = state.investments.map { investment ->
            when (investment) {
                is FixedRateInvestment -> {
                    val newPrincipal = investment.principal * (BigDecimal.ONE + (investment.rate / 12).value)
                    investment.copy(principal = newPrincipal)
                }

                is PersonBoundPromotionalInvestment -> {
                    val promotionStartMonth = state.promotionStartMonths[investment.template]!!
                    val monthsElapsed = state.currentMonth - promotionStartMonth

                    val applicableRate = if (monthsElapsed < investment.template.promotionDurationMonths) {
                        investment.template.promotionalRate
                    } else {
                        investment.template.rate
                    }
                    val newPrincipal = investment.principal * (BigDecimal.ONE + (applicableRate / 12).value)
                    investment.copy(principal = newPrincipal)
                }
            }
        }

        val nextMonth = state.currentMonth + Month.ONE

        return state.copy(
            currentMonth = nextMonth,
            availableCash = state.availableCash + monthlyDeposit,
            investments = updatedInvestments,
        )
    }

    private fun applyInvestDecision(
        state: InvestmentSimulationState,
        decision: InvestmentDecision.Invest,
    ): InvestmentSimulationState {
        require(decision.amount <= state.availableCash) {
            "Cannot invest ${decision.amount} when only ${state.availableCash} is available"
        }

        val newInvestment = when (val type = decision.investmentType) {
            is FixedRateType -> FixedRateInvestment(
                principal = decision.amount,
                investmentMonth = state.currentMonth,
                rate = type.rate,
            )

            is PersonBoundPromotionalType -> {
                PersonBoundPromotionalInvestment(
                    principal = decision.amount,
                    investmentMonth = state.currentMonth,
                    template = type,
                )
            }
        }

        val updatedPromotionStartMonths = if (decision.investmentType is PersonBoundPromotionalType &&
            !state.promotionStartMonths.containsKey(decision.investmentType)
        ) {
            state.promotionStartMonths + (decision.investmentType to state.currentMonth)
        } else {
            state.promotionStartMonths
        }

        return state.copy(
            availableCash = state.availableCash - decision.amount,
            investments = state.investments + newInvestment,
            promotionStartMonths = updatedPromotionStartMonths,
        )
    }

    fun applyWithdrawDecision(state: InvestmentSimulationState): InvestmentSimulationState {
        val withdrawnAmount = state.investments.fold(Money.ZERO) { acc, inv -> acc + inv.currentValue() }
        return state.copy(
            availableCash = state.availableCash + withdrawnAmount,
            investments = emptyList(),
        )
    }
}

