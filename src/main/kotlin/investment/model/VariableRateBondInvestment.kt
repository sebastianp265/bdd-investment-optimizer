package com.github.sebastianp265.investment.model

import com.github.sebastianp265.investment.common.Money
import com.github.sebastianp265.investment.common.Month
import com.github.sebastianp265.investment.model.type.InvestmentType
import com.github.sebastianp265.investment.model.type.VariableRateBondInvestmentType

data class VariableRateBondInvestment(
    override val principal: Money,
    val investmentMonth: Month,
    val type: VariableRateBondInvestmentType,
    val accruedInterest: Money = Money.ZERO,
) : Investment() {

    override fun liquidationValue(): Money {
        val penalty = principal * type.earlyRedemptionPenaltyRate.value
        val payout = principal + accruedInterest - penalty
        return if (payout < principal) principal else payout
    }

    override fun advanceMonth(
        currentMonth: Month,
        promotionStartMonths: Map<InvestmentType, Month>,
    ): InvestmentMonthlyAdvanceResult {
        val monthsElapsed = (currentMonth - investmentMonth).index
        if (monthsElapsed >= type.durationMonths.index) {
            return InvestmentMonthlyAdvanceResult(
                maturedCash = principal + accruedInterest,
                updatedInvestment = null,
            )
        }

        val applicableRate = if (monthsElapsed == 0) {
            type.firstPeriodRate
        } else {
            type.baseRate + type.margin
        }
        val monthlyInterest = principal * (applicableRate / 12).value
        return InvestmentMonthlyAdvanceResult(updatedInvestment = copy(accruedInterest = accruedInterest + monthlyInterest))
    }
}
