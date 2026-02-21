package com.github.sebastianp265.application.simulation

import com.github.sebastianp265.application.strategy.InvestmentStrategy
import com.github.sebastianp265.domain.model.*

class DefaultSimulationEngine(
    private val investmentWallet: InvestmentWallet,
    private val strategy: InvestmentStrategy,
) : SimulationEngine {

    private var currentMonth: Month = Month.ZERO

    override fun step() {
        currentMonth++
        investmentWallet.processMonth(currentMonth)

        val snapshot = investmentWallet.snapshot()
        val actions = strategy.decide(currentMonth, snapshot)

        actions.forEach { investmentWallet.handleCommand(it) }
    }

    override fun run(months: Int): InvestmentWalletSnapshot {
        repeat(months) {
            step()
        }

        return investmentWallet.snapshot()
    }

}