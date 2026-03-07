package com.github.sebastianp265.investment.model

import com.github.sebastianp265.investment.common.Money
import com.github.sebastianp265.investment.common.Month
import com.github.sebastianp265.investment.model.type.VariableRateBondInvestmentType

data class VariableRateBondInvestment(
    override val principal: Money,
    val investmentMonth: Month,
    val type: VariableRateBondInvestmentType,
    val accruedInterest: Money = Money.ZERO,
) : Investment()
