package cli

import com.github.sebastianp265.cli.InvestmentTypeFormattingPolicy
import com.github.sebastianp265.investment.common.Month
import com.github.sebastianp265.investment.common.Rate
import com.github.sebastianp265.investment.model.type.FixedRateInvestmentType
import com.github.sebastianp265.investment.model.type.PersonBoundPromotionalInvestmentType
import com.github.sebastianp265.investment.model.type.VariableRateBondInvestmentType
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class InvestmentTypeFormattingPolicyTest : FunSpec({

    test("formats fixed rate investment type") {
        val type = FixedRateInvestmentType(Rate("0.05".toBigDecimal()))

        InvestmentTypeFormattingPolicy.format(type) shouldBe "Regular Account: 5% APR"
    }

    test("formats promotional investment type") {
        val type = PersonBoundPromotionalInvestmentType(
            rate = Rate("0.05".toBigDecimal()),
            promotionalRate = Rate("0.10".toBigDecimal()),
            promotionDurationMonths = Month(3),
        )

        InvestmentTypeFormattingPolicy.format(type) shouldBe
                "Promotional Account: 10% APR for first 3 months, then 5% APR"
    }

    test("formats variable rate bond investment type") {
        val type = VariableRateBondInvestmentType(
            firstPeriodRate = Rate("0.044".toBigDecimal()),
            baseRate = Rate("0.04".toBigDecimal()),
            margin = Rate("0.0015".toBigDecimal()),
            durationMonths = Month(24),
            earlyRedemptionPenaltyRate = Rate("0.007".toBigDecimal()),
        )

        InvestmentTypeFormattingPolicy.format(type) shouldBe
                "Variable Rate Bond: 4.4% (month 1), then base 4% + 0.15% margin, duration 24 months, penalty 0.7%"
    }
})

