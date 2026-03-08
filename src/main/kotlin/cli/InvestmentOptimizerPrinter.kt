package com.github.sebastianp265.cli

import com.github.sebastianp265.investment.common.toMoney
import com.github.sebastianp265.investment.logic.InvestmentDecision
import com.github.sebastianp265.investment.model.type.InvestmentType
import com.github.sebastianp265.investment.state.InvestmentSimulationState
import java.math.BigDecimal

object InvestmentOptimizerPrinter {

    private const val SEPARATOR = "==================================================================="

    private fun section(title: String, echo: (String) -> Unit) {
        val centered = title.padStart((SEPARATOR.length + title.length) / 2).padEnd(SEPARATOR.length)
        echo(SEPARATOR)
        echo(centered)
        echo(SEPARATOR)
    }

    fun printHeader(echo: (String) -> Unit) {
        section("Investment Optimizer CLI", echo)
        echo("")
    }

    fun printConfiguration(
        initialCash: BigDecimal,
        monthlyDeposit: BigDecimal,
        months: Int,
        algorithm: String,
        echo: (String) -> Unit,
    ) {
        echo("CONFIGURATION:")
        echo("  - Initial capital:    ${initialCash.toMoney()}")
        echo("  - Monthly deposit:    ${monthlyDeposit.toMoney()}")
        echo("  - Time horizon:       $months months")
        echo("  - Algorithm:          $algorithm")
        echo("")
    }

    fun printAvailableInvestmentTypes(types: List<InvestmentType>, echo: (String) -> Unit) {
        echo("AVAILABLE INVESTMENT TYPES:")
        types.forEachIndexed { index, type ->
            echo("  [$index] ${InvestmentTypeFormattingPolicy.format(type)}")
        }
        echo("")
    }

    fun printOptimizationResults(
        bestState: InvestmentSimulationState,
        decisions: List<List<InvestmentDecision>>,
        initialCash: BigDecimal,
        monthlyDeposit: BigDecimal,
        months: Int,
        echo: (String) -> Unit,
    ) {
        section("OPTIMIZATION RESULTS", echo)
        echo("")

        echo("Final value: ${bestState.totalLiquidationValue().value.toMoney()}")
        echo("Final month: ${bestState.currentMonth.index}")
        echo("")

        echo("INVESTMENT DECISIONS BY MONTH:")
        echo("")
        decisions.forEachIndexed { monthIndex, monthDecisions ->
            if (monthDecisions.isEmpty()) {
                echo("Month $monthIndex: [no action]")
            } else {
                echo("Month $monthIndex:")
                monthDecisions.forEach { echo("  > ${InvestmentDecisionFormattingPolicy.format(it)}") }
            }
        }
        echo("")

        section("FINAL PORTFOLIO", echo)
        val totalInvested = initialCash + (monthlyDeposit * BigDecimal(months))
        echo("  - Total invested:    ${totalInvested.toMoney()}")
        echo("  - Total value:       ${bestState.totalLiquidationValue().value.toMoney()}")
        echo("")
    }

}