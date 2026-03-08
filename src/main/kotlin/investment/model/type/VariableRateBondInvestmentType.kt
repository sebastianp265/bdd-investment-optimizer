package com.github.sebastianp265.investment.model.type

import com.github.sebastianp265.investment.common.Money
import com.github.sebastianp265.investment.common.Month
import com.github.sebastianp265.investment.common.Rate
import com.github.sebastianp265.investment.model.VariableRateBondInvestment
import java.math.BigDecimal

data class VariableRateBondInvestmentType(
    val firstPeriodRate: Rate,
    val baseRate: Rate,
    val margin: Rate,
    val durationMonths: Month,
    val earlyRedemptionPenaltyRate: Rate,
) : InvestmentType {

    override fun createInvestment(
        cashToSpend: Money,
        currentMonth: Month,
    ): InvestmentCreationResult {
        val bondNominal = BigDecimal("100")
        val investablePrincipalValue = cashToSpend.value
            .divideToIntegralValue(bondNominal)
            .multiply(bondNominal)
            .setScale(2)
        val remainingCashValue = cashToSpend.value.subtract(investablePrincipalValue).setScale(2)

        return InvestmentCreationResult(
            investment = VariableRateBondInvestment(
                principal = Money(investablePrincipalValue),
                investmentMonth = currentMonth,
                type = this,
            ),
            remainingCash = Money(remainingCashValue),
        )
    }
}
