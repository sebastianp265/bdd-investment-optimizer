package com.github.sebastianp265.domain.command

import com.github.sebastianp265.domain.model.investment.Investment
import com.github.sebastianp265.domain.model.InvestmentId

data class OpenInvestmentCommand(val investmentId: InvestmentId, val investment: Investment) : InvestmentCommand

