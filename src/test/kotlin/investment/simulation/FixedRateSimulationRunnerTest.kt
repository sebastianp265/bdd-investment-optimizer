package investment.simulation

import com.github.sebastianp265.investment.common.Money
import com.github.sebastianp265.investment.common.Month
import com.github.sebastianp265.investment.common.toMoney
import com.github.sebastianp265.investment.common.toRate
import com.github.sebastianp265.investment.logic.InvestmentDecision
import com.github.sebastianp265.investment.model.type.FixedRateInvestmentType
import com.github.sebastianp265.investment.simulation.InvestmentSimulationRunner
import com.github.sebastianp265.investment.state.InvestmentSimulationState
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import java.math.RoundingMode

class FixedRateSimulationRunnerTest : FunSpec({

    test("fixed rate investment with monthly deposits") {
        val initialCash = "20000".toBigDecimal()
        val monthlyDeposit = "1000.00".toBigDecimal()
        val rate = "0.05".toBigDecimal()

        val initialState = InvestmentSimulationState(
            currentMonth = Month.ZERO,
            availableCash = Money(initialCash),
            investments = emptyList()
        )

        val fixedRateInvestmentType = FixedRateInvestmentType(rate.toRate())
        val decisions = listOf(
            listOf(InvestmentDecision.InvestAll(fixedRateInvestmentType)),
            listOf(InvestmentDecision.InvestAll(fixedRateInvestmentType)),
            listOf(InvestmentDecision.InvestAll(fixedRateInvestmentType))
        )

        val finalState = InvestmentSimulationRunner.replay(
            initialState,
            decisions,
            monthlyDeposit.toMoney()
        )

        val ratePerMonth = rate.divide("12".toBigDecimal(), 12, RoundingMode.HALF_UP)

        val inv1 = initialCash * (BigDecimal.ONE + ratePerMonth).pow(3)
        val inv2 = monthlyDeposit * (BigDecimal.ONE + ratePerMonth).pow(2)
        val inv3 = monthlyDeposit * (BigDecimal.ONE + ratePerMonth).pow(1)
        val expectedTotalValue = (inv1 + inv2 + inv3 + monthlyDeposit).setScale(2, RoundingMode.HALF_UP)

        finalState.currentMonth shouldBe Month(3)
        finalState.totalValue().value.shouldBeCloseTo(expectedTotalValue)
    }

    test("fixed rate investment with withdraw decision") {
        val initialCash = "5000.00".toBigDecimal()
        val rate = "0.05".toBigDecimal()

        val savingsAccountType = FixedRateInvestmentType(rate.toRate())
        val initialState = InvestmentSimulationState(
            currentMonth = Month.ZERO,
            availableCash = Money(initialCash),
            investments = emptyList()
        )

        val decisions = listOf(
            listOf(InvestmentDecision.InvestAll(savingsAccountType)),
            listOf(InvestmentDecision.DoNothing),
            listOf(InvestmentDecision.Withdraw),
            listOf(InvestmentDecision.DoNothing)
        )

        val finalState = InvestmentSimulationRunner.replay(
            initialState,
            decisions,
            Money.ZERO
        )

        val monthlyRate = rate.divide("12".toBigDecimal(), 12, RoundingMode.HALF_UP)
        val expectedValue = (initialCash * (BigDecimal.ONE + monthlyRate).pow(2)).setScale(2, RoundingMode.HALF_UP)

        finalState.currentMonth shouldBe Month(4)
        finalState.totalValue().value.shouldBeCloseTo(expectedValue)
    }
})
