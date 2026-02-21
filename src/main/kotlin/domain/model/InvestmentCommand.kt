package com.github.sebastianp265.domain.model

sealed interface InvestmentCommand

data class OpenAccount(val cashAccount: CashAccount) : InvestmentCommand
data class DepositCash(val amount: Money, val toAccount: AccountId) : InvestmentCommand
data class WithdrawCash(val amount: Money, val toAccount: AccountId) : InvestmentCommand

