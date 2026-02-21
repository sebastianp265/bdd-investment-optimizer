package com.github.sebastianp265.application.strategy

import com.github.sebastianp265.domain.value.Month
import com.github.sebastianp265.domain.snapshot.InvestmentWalletSnapshot
import com.github.sebastianp265.domain.command.InvestmentCommand

interface InvestmentStrategy {
    fun decide(currentMonth: Month, investmentWalletSnapshot: InvestmentWalletSnapshot): List<InvestmentCommand>
}

