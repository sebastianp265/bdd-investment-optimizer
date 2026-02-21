package com.github.sebastianp265.domain.command

import com.github.sebastianp265.domain.value.Money
import com.github.sebastianp265.domain.model.InvestmentId

data class DepositCashCommand(val amount: Money, val toInvestmentId: InvestmentId) : InvestmentCommand
