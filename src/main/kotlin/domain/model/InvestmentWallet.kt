package com.github.sebastianp265.domain.model

import com.github.sebastianp265.domain.command.DepositCashCommand
import com.github.sebastianp265.domain.command.InvestmentCommand
import com.github.sebastianp265.domain.command.OpenInvestmentCommand
import com.github.sebastianp265.domain.command.WithdrawCashCommand
import com.github.sebastianp265.domain.model.investment.CashAccount
import com.github.sebastianp265.domain.model.investment.Investment
import com.github.sebastianp265.domain.snapshot.InvestmentWalletSnapshot
import com.github.sebastianp265.domain.value.Month

class InvestmentWallet {
    val investments: MutableMap<InvestmentId, Investment> = mutableMapOf()

    fun snapshot(): InvestmentWalletSnapshot {
        return InvestmentWalletSnapshot(
            investments = investments.mapValues { it.value.snapshot() }
        )
    }

    fun processMonth(currentMonth: Month) {
        // no processing, only cash account supported now
    }

    fun handleCommand(command: InvestmentCommand) {
        when (command) {
            is OpenInvestmentCommand -> {
                require(command.investmentId !in investments)
                investments[command.investmentId] = command.investment
            }

            is DepositCashCommand -> {
                val investment = investments[command.toInvestmentId]
                require(investment != null && investment is CashAccount)
                investment.deposit(command.amount)
            }

            is WithdrawCashCommand -> {
                val investment = investments[command.fromInvestmentId]
                require(investment != null && investment is CashAccount)
                investment.withdraw(command.amount)
            }
        }
    }

}