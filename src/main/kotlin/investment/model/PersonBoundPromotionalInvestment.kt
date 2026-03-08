package com.github.sebastianp265.investment.model

import com.github.sebastianp265.investment.common.Money
import com.github.sebastianp265.investment.common.Month
import com.github.sebastianp265.investment.model.type.InvestmentType
import com.github.sebastianp265.investment.model.type.PersonBoundPromotionalInvestmentType
import java.math.BigDecimal

data class PersonBoundPromotionalInvestment(
    override val principal: Money,
    val investmentMonth: Month,
    val type: PersonBoundPromotionalInvestmentType,
) : Investment() {

    override fun advanceMonth(
        currentMonth: Month,
        promotionStartMonths: Map<InvestmentType, Month>,
    ): InvestmentMonthlyAdvanceResult {
        val promotionStartMonth = promotionStartMonths[type] ?: investmentMonth
        val monthsElapsed = currentMonth - promotionStartMonth
        val applicableRate = if (monthsElapsed < type.promotionDurationMonths) {
            type.promotionalRate
        } else {
            type.rate
        }
        val newPrincipal = principal * (BigDecimal.ONE + (applicableRate / 12).value)
        return InvestmentMonthlyAdvanceResult(updatedInvestment = copy(principal = newPrincipal))
    }

}
