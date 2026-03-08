package com.github.sebastianp265.investment.model

import com.github.sebastianp265.investment.common.Money
import com.github.sebastianp265.investment.common.Month
import com.github.sebastianp265.investment.common.Rate
import com.github.sebastianp265.investment.model.type.InvestmentType
import java.math.BigDecimal

data class FixedRateInvestment(
    override val principal: Money,
    val investmentMonth: Month,
    val rate: Rate,
) : Investment() {

    override fun advanceMonth(
        currentMonth: Month,
        promotionStartMonths: Map<InvestmentType, Month>,
    ): InvestmentMonthlyAdvanceResult {
        val newPrincipal = principal * (BigDecimal.ONE + (rate / 12).value)
        return InvestmentMonthlyAdvanceResult(updatedInvestment = copy(principal = newPrincipal))
    }

}
