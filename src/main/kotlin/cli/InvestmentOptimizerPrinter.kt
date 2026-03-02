package com.github.sebastianp265.cli

import com.github.sebastianp265.investment.model.FixedRateType
import com.github.sebastianp265.investment.model.InvestmentDecision
import com.github.sebastianp265.investment.model.InvestmentType
import com.github.sebastianp265.investment.model.PersonBoundPromotionalType
import com.github.sebastianp265.investment.state.InvestmentSimulationState
import java.math.BigDecimal

object InvestmentOptimizerPrinter {

    private const val SEPARATOR = "==================================================================="

    private fun BigDecimal.toPercent() = (this * BigDecimal(100)).stripTrailingZeros().toPlainString()
    private fun BigDecimal.toMoney() = "$${this.stripTrailingZeros().toPlainString()}"

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
            echo("  [$index] ${formatInvestmentType(type)}")
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

        echo("Final value: ${bestState.totalValue().value.toMoney()}")
        echo("Final month: ${bestState.currentMonth.index}")
        echo("")

        echo("INVESTMENT DECISIONS BY MONTH:")
        echo("")
        decisions.forEachIndexed { monthIndex, monthDecisions ->
            if (monthDecisions.isEmpty()) {
                echo("Month $monthIndex: [no action]")
            } else {
                echo("Month $monthIndex:")
                monthDecisions.forEach { echo("  > ${formatDecision(it)}") }
            }
        }
        echo("")

        section("FINAL PORTFOLIO", echo)
        val totalInvested = initialCash + (monthlyDeposit * BigDecimal(months))
        echo("  - Total invested:    ${totalInvested.toMoney()}")
        echo("  - Total value:       ${bestState.totalValue().value.toMoney()}")
        echo("")
    }

    private fun formatInvestmentType(type: InvestmentType): String {
        return when (type) {
            is FixedRateType ->
                "Regular Account: ${type.rate.value.toPercent()}% APR"

            is PersonBoundPromotionalType ->
                "Promotional Account: ${type.promotionalRate.value.toPercent()}% APR for first ${type.promotionDurationMonths.index} months, then ${type.rate.value.toPercent()}% APR"
        }
    }

    private fun formatDecision(decision: InvestmentDecision): String {
        return when (decision) {
            is InvestmentDecision.Invest ->
                "Invest ${decision.amount.value.toMoney()} in ${formatInvestmentType(decision.investmentType)}"

            is InvestmentDecision.Withdraw ->
                "Withdraw all investments"

            is InvestmentDecision.DoNothing ->
                "Do nothing"
        }
    }
}