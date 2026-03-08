package investment.model

import com.github.sebastianp265.investment.common.Money
import com.github.sebastianp265.investment.common.Month
import com.github.sebastianp265.investment.common.Rate
import com.github.sebastianp265.investment.model.FixedRateInvestment
import com.github.sebastianp265.investment.model.PersonBoundPromotionalInvestment
import com.github.sebastianp265.investment.model.VariableRateBondInvestment
import com.github.sebastianp265.investment.model.type.PersonBoundPromotionalInvestmentType
import com.github.sebastianp265.investment.model.type.VariableRateBondInvestmentType
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class InvestmentAdvanceMonthTest : FunSpec({

    test("advanceMonth accrues fixed-rate investment") {
        val investment = FixedRateInvestment(
            principal = Money("1000.00".toBigDecimal()),
            investmentMonth = Month.ZERO,
            rate = Rate("0.06".toBigDecimal()),
        )

        val update = investment.advanceMonth(
            currentMonth = Month.ZERO,
            promotionStartMonths = emptyMap(),
        )

        update.maturedCash shouldBe Money.ZERO
        (update.updatedInvestment as FixedRateInvestment).principal.value shouldBe "1005.00".toBigDecimal()
    }

    test("advanceMonth pays matured bond into cash and removes investment") {
        val bondType = VariableRateBondInvestmentType(
            firstPeriodRate = Rate("0.044".toBigDecimal()),
            baseRate = Rate("0.04".toBigDecimal()),
            margin = Rate("0.0015".toBigDecimal()),
            durationMonths = Month(2),
            earlyRedemptionPenaltyRate = Rate("0.007".toBigDecimal()),
        )
        val investment = VariableRateBondInvestment(
            principal = Money("1000.00".toBigDecimal()),
            investmentMonth = Month.ZERO,
            type = bondType,
            accruedInterest = Money("10.00".toBigDecimal()),
        )

        val update = investment.advanceMonth(
            currentMonth = Month(2),
            promotionStartMonths = emptyMap(),
        )

        update.maturedCash.value shouldBe "1010.00".toBigDecimal()
        update.updatedInvestment shouldBe null
    }

    test("advanceMonth uses promotional rate while promotion is active") {
        val promoType = PersonBoundPromotionalInvestmentType(
            rate = Rate("0.05".toBigDecimal()),
            promotionalRate = Rate("0.10".toBigDecimal()),
            promotionDurationMonths = Month(3),
        )
        val investment = PersonBoundPromotionalInvestment(
            principal = Money("1000.00".toBigDecimal()),
            investmentMonth = Month.ZERO,
            type = promoType,
        )

        val update = investment.advanceMonth(
            currentMonth = Month(2),
            promotionStartMonths = mapOf(promoType to Month.ZERO),
        )

        (update.updatedInvestment as PersonBoundPromotionalInvestment).principal.value shouldBe "1008.33".toBigDecimal()
    }
})

