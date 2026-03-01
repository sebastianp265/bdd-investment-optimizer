package investment.simulation

import com.github.sebastianp265.investment.common.Money
import com.github.sebastianp265.investment.common.Month
import com.github.sebastianp265.investment.common.Rate
import com.github.sebastianp265.investment.model.FixedRateInvestment
import com.github.sebastianp265.investment.model.FixedRateType
import com.github.sebastianp265.investment.model.InvestmentDecision
import com.github.sebastianp265.investment.model.PersonBoundPromotionalType
import com.github.sebastianp265.investment.simulation.InvestmentSimulationRunner
import com.github.sebastianp265.investment.state.InvestmentSimulationState
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.bigdecimal.shouldBeGreaterThan
import io.kotest.matchers.bigdecimal.shouldBeLessThanOrEqual
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import java.math.RoundingMode

class InvestmentSimulationRunnerTest : FunSpec({

    test("saving in regular fixed rate account with monthly deposits") {
        val initialCash = Money(BigDecimal("20000.00"))
        val monthlyDeposit = Money(BigDecimal("1000.00"))
        val rate = Rate(BigDecimal("0.05"))

        val savingsAccountTemplate = FixedRateType(rate)

        val initialState = InvestmentSimulationState(
            currentMonth = Month.ZERO,
            availableCash = initialCash,
            investments = emptyList()
        )

        val decisions = listOf(
            listOf(InvestmentDecision.Invest(savingsAccountTemplate, initialCash)),
            listOf(InvestmentDecision.DoNothing),
            listOf(InvestmentDecision.DoNothing)
        )

        val finalState = InvestmentSimulationRunner.replay(
            initialState,
            decisions,
            monthlyDeposit
        )

        finalState.currentMonth shouldBe Month(3)
        finalState.investments.size shouldBe 1
        finalState.availableCash.value shouldBe monthlyDeposit.value.multiply(BigDecimal("3"))
    }

    test("replay handles withdraw decision") {
        val initialCash = Money(BigDecimal("5000.00"))
        val rate = Rate(BigDecimal("0.05"))

        val savingsAccount = FixedRateInvestment(
            principal = Money(BigDecimal("5000.00")),
            investmentMonth = Month.ZERO,
            rate = rate
        )

        val initialState = InvestmentSimulationState(
            currentMonth = Month.ZERO,
            availableCash = initialCash,
            investments = listOf(savingsAccount)
        )

        val decisions = listOf(
            listOf(InvestmentDecision.Withdraw)
        )

        val finalState = InvestmentSimulationRunner.replay(
            initialState,
            decisions,
            Money.ZERO
        )

        finalState.currentMonth shouldBe Month(1)
        finalState.investments shouldBe emptyList()
        finalState.availableCash.value shouldBeGreaterThan initialCash.value
    }

    test("replay with multiple investments and monthly deposits calculates final value correctly") {
        val initialCash = Money(BigDecimal("20000.00"))
        val monthlyDeposit = Money(BigDecimal("1000.00"))
        val rate = BigDecimal("0.05")
        val fixedRateType = FixedRateType(Rate(rate))

        val initialState = InvestmentSimulationState(
            currentMonth = Month.ZERO,
            availableCash = initialCash,
            investments = emptyList(),
        )

        val monthlyRate = rate.divide(BigDecimal("12"), 12, RoundingMode.HALF_UP)
        val inv1 = BigDecimal("20000.00") * BigDecimal.ONE.add(monthlyRate).pow(3)
        val inv2 = BigDecimal("1000.00") * BigDecimal.ONE.add(monthlyRate).pow(2)
        val inv3 = BigDecimal("1000.00") * BigDecimal.ONE.add(monthlyRate).pow(1)
        val cash = BigDecimal("1000.00")

        val actualState = InvestmentSimulationRunner.replay(
            initialState,
            listOf(
                listOf(InvestmentDecision.Invest(fixedRateType, initialCash)),
                listOf(InvestmentDecision.Invest(fixedRateType, monthlyDeposit)),
                listOf(InvestmentDecision.Invest(fixedRateType, monthlyDeposit))
            ),
            monthlyDeposit
        )

        val expectedValue = (inv1 + inv2 + inv3 + cash).setScale(2, RoundingMode.HALF_UP)

        actualState.currentMonth shouldBe Month(3)
        actualState.totalValue().value shouldBeEqual expectedValue
    }

    test("promotional account applies bonus rate for first 3 months, then normal rate") {
        val initialCash = Money(BigDecimal("10000.00"))
        val monthlyDeposit = Money(BigDecimal("1000.00"))
        val normalRate = Rate(BigDecimal("0.03"))
        val promotionalRate = Rate(BigDecimal("0.08"))
        val promotionDurationMonths = Month(3)

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

        value += monthlyDeposit.value
        value *= (BigDecimal.ONE + monthlyPromoRate)

        value += monthlyDeposit.value
        value *= (BigDecimal.ONE + monthlyPromoRate)

        value += monthlyDeposit.value
        value *= (BigDecimal.ONE + monthlyNormalRate)

        value += monthlyDeposit.value

        val expectedValue = value.setScale(2, RoundingMode.HALF_UP)

        val decisions = listOf(
            listOf(InvestmentDecision.Invest(promotionalType, initialCash)),
            listOf(InvestmentDecision.Invest(promotionalType, monthlyDeposit)),
            listOf(InvestmentDecision.Invest(promotionalType, monthlyDeposit)),
            listOf(InvestmentDecision.Invest(promotionalType, monthlyDeposit)),
        )

        val finalState = InvestmentSimulationRunner.replay(
            initialState,
            decisions,
            monthlyDeposit,
        )

        finalState.currentMonth shouldBe Month(4)
        // TODO: To fix this: this should be precisely calculated, but currently logic handles savings account as separate investments, but they should be shared
        (finalState.totalValue().value - expectedValue).abs() shouldBeLessThanOrEqual BigDecimal("0.01")
    }

})