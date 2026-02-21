package com.github.sebastianp265.domain.command

import com.github.sebastianp265.domain.value.Money
import com.github.sebastianp265.domain.model.InvestmentId

data class WithdrawCashCommand(val amount: Money, val fromInvestmentId: InvestmentId) : InvestmentCommand
