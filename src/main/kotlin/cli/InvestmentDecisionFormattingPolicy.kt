package com.github.sebastianp265.cli

import com.github.sebastianp265.investment.logic.InvestmentDecision

object InvestmentDecisionFormattingPolicy {
    fun format(decision: InvestmentDecision): String {
        return when (decision) {
            is InvestmentDecision.InvestAll ->
                "Invest all in ${InvestmentTypeFormattingPolicy.format(decision.investmentType)}"

            is InvestmentDecision.Withdraw ->
                "Withdraw all investments"

            is InvestmentDecision.DoNothing ->
                "Do nothing"
        }
    }
}

