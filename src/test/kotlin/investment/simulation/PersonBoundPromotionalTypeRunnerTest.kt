package investment.simulation

import com.github.sebastianp265.investment.common.Month
import com.github.sebastianp265.investment.common.toMoney
import com.github.sebastianp265.investment.common.toRate
import com.github.sebastianp265.investment.logic.InvestmentDecision
import com.github.sebastianp265.investment.model.type.PersonBoundPromotionalInvestmentType
import com.github.sebastianp265.investment.simulation.InvestmentSimulationRunner
import com.github.sebastianp265.investment.state.InvestmentSimulationState
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import java.math.RoundingMode

class PersonBoundPromotionalTypeRunnerTest : FunSpec({

    test("person bound promotional investment") {
        val initialCash = "20000".toBigDecimal()
        val monthlyDeposit = "1000".toBigDecimal()
        val normalRate = "0.05".toBigDecimal()
        val promotionalRate = "0.10".toBigDecimal()
        val promotionDurationMonths = Month(3)

        val promotionalType = PersonBoundPromotionalInvestmentType(
            rate = normalRate.toRate(),
            promotionalRate = promotionalRate.toRate(),
            promotionDurationMonths = promotionDurationMonths
        )

        val initialState = InvestmentSimulationState(
            currentMonth = Month.ZERO,
            availableCash = initialCash.toMoney(),
            investments = emptyList()
        )

        val decisions = listOf(
            listOf(InvestmentDecision.InvestAll(promotionalType)),
            listOf(InvestmentDecision.InvestAll(promotionalType)),
            listOf(InvestmentDecision.InvestAll(promotionalType)),
            listOf(InvestmentDecision.InvestAll(promotionalType)),
        )

        val finalState = InvestmentSimulationRunner.replay(
            initialState,
            decisions,
            monthlyDeposit.toMoney(),
        )

        val monthlyNormalRate = normalRate.divide("12".toBigDecimal(), 12, RoundingMode.HALF_UP)
        val monthlyPromoRate = promotionalRate.divide("12".toBigDecimal(), 12, RoundingMode.HALF_UP)

        val inv1 = initialCash * (BigDecimal.ONE + monthlyPromoRate).pow(3) * (BigDecimal.ONE + monthlyNormalRate)
        val inv2 = monthlyDeposit * (BigDecimal.ONE + monthlyPromoRate).pow(2) * (BigDecimal.ONE + monthlyNormalRate)
        val inv3 = monthlyDeposit * (BigDecimal.ONE + monthlyPromoRate).pow(1) * (BigDecimal.ONE + monthlyNormalRate)
        val inv4 = monthlyDeposit * (BigDecimal.ONE + monthlyNormalRate)

        val expectedValue = (inv1 + inv2 + inv3 + inv4 + monthlyDeposit).setScale(2, RoundingMode.HALF_UP)

        finalState.currentMonth shouldBe Month(4)
        finalState.totalValue().value shouldBe expectedValue
    }


    test("person bound promotional investment with withdraw after month then investing again") {
        val initialCash = "20000".toBigDecimal()
        val monthlyDeposit = "1000".toBigDecimal()
        val normalRate = "0.05".toBigDecimal()
        val promotionalRate = "0.10".toBigDecimal()
        val promotionDurationMonths = Month(3)

        val promotionalType = PersonBoundPromotionalInvestmentType(
            rate = normalRate.toRate(),
            promotionalRate = promotionalRate.toRate(),
            promotionDurationMonths = promotionDurationMonths
        )

        val initialState = InvestmentSimulationState(
            currentMonth = Month.ZERO,
            availableCash = initialCash.toMoney(),
            investments = emptyList()
        )

        val decisions = listOf(
            listOf(InvestmentDecision.InvestAll(promotionalType)),
            listOf(InvestmentDecision.Withdraw),
            listOf(InvestmentDecision.InvestAll(promotionalType)),
            listOf(InvestmentDecision.DoNothing),
        )

        val finalState = InvestmentSimulationRunner.replay(
            initialState,
            decisions,
            monthlyDeposit.toMoney(),
        )

        val monthlyNormalRate = normalRate.divide("12".toBigDecimal(), 12, RoundingMode.HALF_UP)
        val monthlyPromoRate = promotionalRate.divide("12".toBigDecimal(), 12, RoundingMode.HALF_UP)

        val inv1 = initialCash * (BigDecimal.ONE + monthlyPromoRate)
        val inv2 =
            (inv1 + monthlyDeposit + monthlyDeposit) * (BigDecimal.ONE + monthlyPromoRate) * (BigDecimal.ONE + monthlyNormalRate)

        val expectedValue = (inv2 + monthlyDeposit + monthlyDeposit).setScale(2, RoundingMode.HALF_UP)

        finalState.currentMonth shouldBe Month(4)
        finalState.totalValue().value shouldBe expectedValue
    }
})