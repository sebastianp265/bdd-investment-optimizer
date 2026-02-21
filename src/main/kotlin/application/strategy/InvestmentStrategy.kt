package com.github.sebastianp265.application.strategy

import com.github.sebastianp265.domain.model.InvestmentCommand
import com.github.sebastianp265.domain.model.Month
import com.github.sebastianp265.domain.model.InvestmentWalletSnapshot

interface InvestmentStrategy {
    fun decide(currentMonth: Month, investmentWalletSnapshot: InvestmentWalletSnapshot): List<InvestmentCommand>
}

