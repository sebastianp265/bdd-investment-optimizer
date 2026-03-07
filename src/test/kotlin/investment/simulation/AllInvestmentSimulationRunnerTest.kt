package investment.simulation

import com.github.sebastianp265.investment.common.Month
import com.github.sebastianp265.investment.common.toMoney
import com.github.sebastianp265.investment.common.toRate
import com.github.sebastianp265.investment.logic.InvestmentDecision
import com.github.sebastianp265.investment.model.type.FixedRateInvestmentType
import com.github.sebastianp265.investment.model.type.PersonBoundPromotionalInvestmentType
import com.github.sebastianp265.investment.simulation.InvestmentSimulationRunner
import com.github.sebastianp265.investment.state.InvestmentSimulationState
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import java.math.RoundingMode

class AllInvestmentSimulationRunnerTest : FunSpec({

    test("mixed fixed rate and person bound promotional investments") {
        val initialCash = "20000".toBigDecimal()
        val monthlyDeposit = "1000".toBigDecimal()

        val normalRate = "0.05".toBigDecimal()
        val promotionalRate = "0.10".toBigDecimal()
        val fixedRate = "0.06".toBigDecimal()

        val promotionalType = PersonBoundPromotionalInvestmentType(
            rate = normalRate.toRate(),
            promotionalRate = promotionalRate.toRate(),
            promotionDurationMonths = Month(3),
        )
        val fixedRateType = FixedRateInvestmentType(fixedRate.toRate())

        val initialState = InvestmentSimulationState(
            currentMonth = Month.ZERO,
            availableCash = initialCash.toMoney(),
            investments = emptyList(),
        )

        val decisions = listOf(
            listOf(InvestmentDecision.InvestAll(promotionalType)),
            listOf(InvestmentDecision.InvestAll(fixedRateType)),
            listOf(InvestmentDecision.InvestAll(promotionalType)),
            listOf(InvestmentDecision.DoNothing),
        )

        val finalState = InvestmentSimulationRunner.replay(
            initialState,
            decisions,
            monthlyDeposit.toMoney(),
        )

        val monthlyPromoRate = promotionalRate.divide("12".toBigDecimal(), 12, RoundingMode.HALF_UP)
        val monthlyNormalRate = normalRate.divide("12".toBigDecimal(), 12, RoundingMode.HALF_UP)
        val monthlyFixedRate = fixedRate.divide("12".toBigDecimal(), 12, RoundingMode.HALF_UP)

        val promoInv1 = initialCash * (BigDecimal.ONE + monthlyPromoRate).pow(3) * (BigDecimal.ONE + monthlyNormalRate)

        val fixedInv = monthlyDeposit * (BigDecimal.ONE + monthlyFixedRate).pow(3)

        val promoInv2 = monthlyDeposit * (BigDecimal.ONE + monthlyPromoRate) * (BigDecimal.ONE + monthlyNormalRate)

        val expectedValue = (promoInv1 + fixedInv + promoInv2 + monthlyDeposit + monthlyDeposit)
            .setScale(2, RoundingMode.HALF_UP)

        finalState.currentMonth shouldBe Month(4)
        finalState.totalValue().value.shouldBeCloseTo(expectedValue)
    }

})
