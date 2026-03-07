package com.github.sebastianp265.investment.logic

import com.github.sebastianp265.graph.Transition
import com.github.sebastianp265.investment.common.Money
import com.github.sebastianp265.investment.common.Month
import com.github.sebastianp265.investment.model.FixedRateInvestment
import com.github.sebastianp265.investment.model.PersonBoundPromotionalInvestment
import com.github.sebastianp265.investment.model.VariableRateBondInvestment
import com.github.sebastianp265.investment.model.type.FixedRateInvestmentType
import com.github.sebastianp265.investment.model.type.PersonBoundPromotionalInvestmentType
import com.github.sebastianp265.investment.model.type.VariableRateBondInvestmentType
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
                is InvestmentDecision.InvestAll -> applyInvestAllDecision(currentState, decision)
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
        val maturedCash = state.investments
            .filterIsInstance<VariableRateBondInvestment>()
            .filter { (state.currentMonth - it.investmentMonth).index >= it.type.durationMonths.index }
            .fold(Money.ZERO) { acc, inv -> acc + inv.principal + inv.accruedInterest }

        val updatedInvestments = state.investments
            .filter { inv ->
                inv !is VariableRateBondInvestment || (state.currentMonth - inv.investmentMonth).index < inv.type.durationMonths.index
            }
            .map { investment ->
                when (investment) {
                    is FixedRateInvestment -> {
                        val newPrincipal = investment.principal * (BigDecimal.ONE + (investment.rate / 12).value)
                        investment.copy(principal = newPrincipal)
                    }

                    is PersonBoundPromotionalInvestment -> {
                        val promotionStartMonth = state.promotionStartMonths[investment.type]!!
                        val monthsElapsed = state.currentMonth - promotionStartMonth

                        val applicableRate = if (monthsElapsed < investment.type.promotionDurationMonths) {
                            investment.type.promotionalRate
                        } else {
                            investment.type.rate
                        }
                        val newPrincipal = investment.principal * (BigDecimal.ONE + (applicableRate / 12).value)
                        investment.copy(principal = newPrincipal)
                    }

                    is VariableRateBondInvestment -> {
                        val monthsElapsed = (state.currentMonth - investment.investmentMonth).index
                        val applicableRate = if (monthsElapsed == 0) {
                            investment.type.firstPeriodRate
                        } else {
                            investment.type.baseRate + investment.type.margin
                        }
                        val monthlyInterest = investment.principal * (applicableRate / 12).value
                        investment.copy(accruedInterest = investment.accruedInterest + monthlyInterest)
                    }
                }
            }

        val nextMonth = state.currentMonth + Month.ONE

        return state.copy(
            currentMonth = nextMonth,
            availableCash = state.availableCash + monthlyDeposit + maturedCash,
            investments = updatedInvestments,
        )
    }

    private fun applyInvestAllDecision(
        state: InvestmentSimulationState,
        decision: InvestmentDecision.InvestAll,
    ): InvestmentSimulationState {
        val newInvestment = when (val type = decision.investmentType) {
            is FixedRateInvestmentType -> FixedRateInvestment(
                principal = state.availableCash,
                investmentMonth = state.currentMonth,
                rate = type.rate,
            )

            is PersonBoundPromotionalInvestmentType -> PersonBoundPromotionalInvestment(
                principal = state.availableCash,
                investmentMonth = state.currentMonth,
                type = type,
            )

            // TODO: This is wrong XD
            is VariableRateBondInvestmentType -> VariableRateBondInvestment(
                principal = state.availableCash,
                investmentMonth = state.currentMonth,
                type = type,
            )
        }

        val updatedPromotionStartMonths = if (decision.investmentType is PersonBoundPromotionalInvestmentType &&
            !state.promotionStartMonths.containsKey(decision.investmentType)
        ) {
            state.promotionStartMonths + (decision.investmentType to state.currentMonth)
        } else {
            state.promotionStartMonths
        }

        return state.copy(
            availableCash = Money.ZERO,
            investments = state.investments + newInvestment,
            promotionStartMonths = updatedPromotionStartMonths,
        )
    }

    fun applyWithdrawDecision(state: InvestmentSimulationState): InvestmentSimulationState {
        val withdrawnAmount = state.investments.fold(Money.ZERO) { acc, inv ->
            acc + when (inv) {
                is VariableRateBondInvestment -> {
                    val penalty = inv.principal * inv.type.earlyRedemptionPenaltyRate.value
                    val payout = inv.principal + inv.accruedInterest - penalty
                    if (payout < inv.principal) inv.principal else payout
                }

                else -> inv.currentValue()
            }
        }
        return state.copy(
            availableCash = state.availableCash + withdrawnAmount,
            investments = emptyList(),
        )
    }
}
