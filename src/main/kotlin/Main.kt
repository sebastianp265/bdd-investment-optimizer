package com.github.sebastianp265

import com.github.sebastianp265.application.simulation.DefaultSimulationEngine
import com.github.sebastianp265.application.strategy.InvestmentStrategy
import com.github.sebastianp265.domain.model.AccountId
import com.github.sebastianp265.domain.model.DepositCash
import com.github.sebastianp265.domain.model.InvestmentCommand
import com.github.sebastianp265.domain.model.InvestmentWallet
import com.github.sebastianp265.domain.model.InvestmentWalletSnapshot
import com.github.sebastianp265.domain.model.Money
import com.github.sebastianp265.domain.model.Month

enum class InvestmentType {
    REGULAR_CASH_ACCOUNT;

    fun accountId(): AccountId = AccountId(this.name)
}

fun main() {
    val wallet = InvestmentWallet()
    val investmentStrategy: InvestmentStrategy = object : InvestmentStrategy {
        override fun decide(
            currentMonth: Month,
            investmentWalletSnapshot: InvestmentWalletSnapshot
        ): List<InvestmentCommand> {
            val monthlyCash = Money(2000.toBigDecimal())
            return listOf(DepositCash(monthlyCash, InvestmentType.REGULAR_CASH_ACCOUNT.accountId()))
        }
    }
    val simulationEngine = DefaultSimulationEngine(wallet, investmentStrategy)

    println(simulationEngine.run(2).cashAccounts)
}