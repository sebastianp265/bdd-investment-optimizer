package com.github.sebastianp265.domain.model

data class InvestmentWalletSnapshot(
    val cashAccounts: Set<CashAccount>,
)

class InvestmentWallet {
    val openCashAccounts: MutableSet<CashAccount> = mutableSetOf()

    fun snapshot(): InvestmentWalletSnapshot {
        return InvestmentWalletSnapshot(
            cashAccounts = openCashAccounts
        )
    }

    fun processMonth(currentMonth: Month) {
        // no processing, only cash account supported now
    }

    fun handleCommand(command: InvestmentCommand) {
        when (command) {
            is DepositCash -> {
                openCashAccounts.first { it.id == command.toAccount }
                    .deposit(command.amount)
            }
            is WithdrawCash -> {
                openCashAccounts.first { it.id == command.toAccount }
                    .withdraw(command.amount)
            }
            is OpenAccount -> {
                require(command.cashAccount !in openCashAccounts)
                openCashAccounts.add(command.cashAccount)
            }
        }
    }

}
