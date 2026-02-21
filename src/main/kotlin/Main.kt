package com.github.sebastianp265

import com.github.sebastianp265.application.simulation.DefaultSimulationEngine
import com.github.sebastianp265.application.strategy.InvestmentStrategy
import com.github.sebastianp265.domain.command.DepositCashCommand
import com.github.sebastianp265.domain.command.InvestmentCommand
import com.github.sebastianp265.domain.command.OpenInvestmentCommand
import com.github.sebastianp265.domain.model.InvestmentId
import com.github.sebastianp265.domain.model.InvestmentWallet
import com.github.sebastianp265.domain.model.investment.CashAccount
import com.github.sebastianp265.domain.snapshot.InvestmentWalletSnapshot
import com.github.sebastianp265.domain.value.Money
import com.github.sebastianp265.domain.value.Month

enum class InvestmentType {
    REGULAR_CASH_ACCOUNT;

    fun investmentId() = InvestmentId(this.name)
}

fun main() {
    val wallet = InvestmentWallet()
    wallet.handleCommand(
        OpenInvestmentCommand(
            InvestmentType.REGULAR_CASH_ACCOUNT.investmentId(),
            CashAccount(),
        )
    )

    val investmentStrategy: InvestmentStrategy = object : InvestmentStrategy {
        override fun decide(
            currentMonth: Month,
            investmentWalletSnapshot: InvestmentWalletSnapshot
        ): List<InvestmentCommand> {
            val monthlyCash = Money(2000.toBigDecimal())
            return listOf(DepositCashCommand(monthlyCash, InvestmentType.REGULAR_CASH_ACCOUNT.investmentId()))
        }
    }
    val simulationEngine = DefaultSimulationEngine(wallet, investmentStrategy)

    println(simulationEngine.run(2).investments)
}