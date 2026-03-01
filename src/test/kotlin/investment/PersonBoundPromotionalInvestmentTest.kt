package investment

import com.github.sebastianp265.investment.*
import com.github.sebastianp265.investment.common.Money
import com.github.sebastianp265.investment.common.Month
import com.github.sebastianp265.investment.common.Rate
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import java.math.RoundingMode

class PersonBoundPromotionalInvestmentTest : FunSpec({

    test("promotional account applies bonus rate for first 3 months, then normal rate") {
        val initialCash = Money(BigDecimal("10000.00"))
        val normalRate = Rate(BigDecimal("0.03"))
        val promotionalRate = Rate(BigDecimal("0.08"))
        val promotionDurationMonths = 3

        val promotionalType = PersonBoundPromotionalType(
            rate = normalRate,
            promotionalRate = promotionalRate,
            promotionDurationMonths = promotionDurationMonths
        )

        val initialState = InvestmentSimulationState(
            currentMonth = Month.ZERO,
            availableCash = initialCash,
            investments = emptyList()
        )

        val monthlyPromoRate = (promotionalRate / 12).value
        val monthlyNormalRate = (normalRate / 12).value

        var value = BigDecimal("10000.00")
        value *= (BigDecimal.ONE + monthlyPromoRate)
        value *= (BigDecimal.ONE + monthlyPromoRate)
        value *= (BigDecimal.ONE + monthlyPromoRate)
        value *= (BigDecimal.ONE + monthlyNormalRate)

        val expectedValue = value.setScale(2, RoundingMode.HALF_UP)

        val decisions = listOf(
            listOf(InvestmentDecision.Invest(promotionalType, initialCash)),
            listOf(InvestmentDecision.DoNothing),
            listOf(InvestmentDecision.DoNothing),
            listOf(InvestmentDecision.DoNothing),
        )

        val finalState = InvestmentSimulationRunner.replay(
            initialState,
            decisions,
            Money.ZERO
        )

        finalState.currentMonth shouldBe Month(4)
        finalState.totalValue().value shouldBe expectedValue
    }

})






