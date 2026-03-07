package com.github.sebastianp265.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
import com.github.sebastianp265.investment.common.Money
import com.github.sebastianp265.investment.common.Month
import com.github.sebastianp265.investment.common.Rate
import com.github.sebastianp265.investment.model.type.FixedRateInvestmentType
import com.github.sebastianp265.investment.model.type.PersonBoundPromotionalInvestmentType
import com.github.sebastianp265.investment.model.type.VariableRateBondInvestmentType
import com.github.sebastianp265.investment.simulation.InvestmentSimulation
import com.github.sebastianp265.investment.state.InvestmentSimulationState
import com.github.sebastianp265.optimizer.DFSOptimizationEngine
import java.math.BigDecimal

class InvestmentOptimizerCLI : CliktCommand(help = "Optimize investment strategy") {
    private val initialCash: BigDecimal by option(
        "--initial", "-i",
        help = "Initial capital amount (default: 10000)",
    ).convert { BigDecimal(it) }.default(BigDecimal("10000"))

    private val monthlyDeposit: BigDecimal by option(
        "--monthly", "-m",
        help = "Monthly deposit amount (default: 1000)",
    ).convert { BigDecimal(it) }.default(BigDecimal("1000"))

    private val months: Int by option(
        "--months", "-n",
        help = "Number of months to simulate (default: 6)",
    ).int().default(6)

    private val algorithm: String by option(
        "--algorithm", "-a",
        help = "Optimization algorithm (default: dfs)",
    ).default("dfs")

    override fun run() {
        val availableInvestmentTypes = listOf(
            FixedRateInvestmentType(Rate(BigDecimal("0.02"))),
            PersonBoundPromotionalInvestmentType(
                rate = Rate(BigDecimal("0.01")),
                promotionalRate = Rate(BigDecimal("0.05")),
                promotionDurationMonths = Month(3),
            ),
            VariableRateBondInvestmentType(
                firstPeriodRate = Rate(BigDecimal("0.044")),
                baseRate = Rate(BigDecimal("0.04")),
                margin = Rate(BigDecimal("0.0015")),
                durationMonths = Month(24),
                earlyRedemptionPenaltyRate = Rate(BigDecimal("0.007")),
            ),
        )
        val simulation = InvestmentSimulation(
            finalMonth = Month(months),
            availableInvestmentTypes = availableInvestmentTypes,
            monthlyDeposit = Money(monthlyDeposit),
        )
        val initialState = InvestmentSimulationState(
            currentMonth = Month.Companion.ZERO,
            availableCash = Money(initialCash),
            investments = emptyList(),
        )

        InvestmentOptimizerPrinter.printHeader(::echo)
        InvestmentOptimizerPrinter.printConfiguration(initialCash, monthlyDeposit, months, algorithm, ::echo)
        InvestmentOptimizerPrinter.printAvailableInvestmentTypes(availableInvestmentTypes, ::echo)

        echo("Optimizing...")
        val (bestState, decisions) = DFSOptimizationEngine.optimize(simulation, initialState)
        echo()

        InvestmentOptimizerPrinter.printOptimizationResults(
            bestState,
            decisions,
            initialCash,
            monthlyDeposit,
            months,
            ::echo
        )
    }
}