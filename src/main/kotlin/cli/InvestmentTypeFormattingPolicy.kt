package com.github.sebastianp265.cli

import com.github.sebastianp265.investment.model.type.FixedRateInvestmentType
import com.github.sebastianp265.investment.model.type.InvestmentType
import com.github.sebastianp265.investment.model.type.PersonBoundPromotionalInvestmentType
import com.github.sebastianp265.investment.model.type.VariableRateBondInvestmentType
import java.math.BigDecimal

object InvestmentTypeFormattingPolicy {
    private fun BigDecimal.toPercent() = (this * BigDecimal(100)).stripTrailingZeros().toPlainString()

    fun format(type: InvestmentType): String {
        return when (type) {
            is FixedRateInvestmentType ->
                "Regular Account: ${type.rate.value.toPercent()}% APR"

            is PersonBoundPromotionalInvestmentType ->
                "Promotional Account: ${type.promotionalRate.value.toPercent()}% APR for first ${type.promotionDurationMonths.index} months, then ${type.rate.value.toPercent()}% APR"

            is VariableRateBondInvestmentType ->
                "Variable Rate Bond: ${type.firstPeriodRate.value.toPercent()}% (month 1), then base ${type.baseRate.value.toPercent()}% + ${type.margin.value.toPercent()}% margin, duration ${type.durationMonths.index} months, penalty ${type.earlyRedemptionPenaltyRate.value.toPercent()}%"
        }
    }
}
