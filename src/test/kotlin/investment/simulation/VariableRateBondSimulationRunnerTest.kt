package investment.simulation

import com.github.sebastianp265.investment.common.Month
import com.github.sebastianp265.investment.common.toMoney
import com.github.sebastianp265.investment.common.toRate
import com.github.sebastianp265.investment.logic.InvestmentDecision
import com.github.sebastianp265.investment.model.VariableRateBondInvestment
import com.github.sebastianp265.investment.model.type.VariableRateBondInvestmentType
import com.github.sebastianp265.investment.simulation.InvestmentSimulationRunner
import com.github.sebastianp265.investment.state.InvestmentSimulationState
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import java.math.RoundingMode

class VariableRateBondSimulationRunnerTest : FunSpec({

    test("variable rate bond matures and pays out principal with accrued interest") {
        val initialCash = "10000".toBigDecimal()
        val monthlyDeposit = "1000".toBigDecimal()

        val firstPeriodRate = "0.044".toBigDecimal()
        val baseRate = "0.04".toBigDecimal()
        val margin = "0.0015".toBigDecimal()

        val bondType = VariableRateBondInvestmentType(
            firstPeriodRate = firstPeriodRate.toRate(),
            baseRate = baseRate.toRate(),
            margin = margin.toRate(),
            durationMonths = Month(3),
            earlyRedemptionPenaltyRate = "0.007".toBigDecimal().toRate(),
        )

        val initialState = InvestmentSimulationState(
            currentMonth = Month.ZERO,
            availableCash = initialCash.toMoney(),
            investments = emptyList(),
        )

        val decisions = listOf(
            listOf(InvestmentDecision.InvestAll(bondType)),
            listOf(InvestmentDecision.DoNothing),
            listOf(InvestmentDecision.DoNothing),
            listOf(InvestmentDecision.DoNothing),
        )

        val finalState = InvestmentSimulationRunner.replay(
            initialState,
            decisions,
            monthlyDeposit.toMoney(),
        )

        val firstPeriodMonthlyRate = firstPeriodRate.divide("12".toBigDecimal(), 12, RoundingMode.HALF_UP)
        val regularMonthlyRate = (baseRate + margin).divide("12".toBigDecimal(), 12, RoundingMode.HALF_UP)

        val firstMonthInterest = (initialCash * firstPeriodMonthlyRate).setScale(2, RoundingMode.HALF_UP)
        val secondMonthInterest = (initialCash * regularMonthlyRate).setScale(2, RoundingMode.HALF_UP)
        val thirdMonthInterest = (initialCash * regularMonthlyRate).setScale(2, RoundingMode.HALF_UP)

        val expectedValue = (
                initialCash +
                        firstMonthInterest +
                        secondMonthInterest +
                        thirdMonthInterest +
                        monthlyDeposit * "4".toBigDecimal()
                ).setScale(2, RoundingMode.HALF_UP)

        finalState.currentMonth shouldBe Month(4)
        finalState.totalValue().value.shouldBeCloseTo(expectedValue)
    }

    test("variable rate bond withdraw keeps principal floor when penalty is higher than accrued interest") {
        val initialCash = "10000".toBigDecimal()
        val firstPeriodRate = "0.044".toBigDecimal()
        val penaltyRate = "0.007".toBigDecimal()

        val bondType = VariableRateBondInvestmentType(
            firstPeriodRate = firstPeriodRate.toRate(),
            baseRate = "0.04".toBigDecimal().toRate(),
            margin = "0.0015".toBigDecimal().toRate(),
            durationMonths = Month(24),
            earlyRedemptionPenaltyRate = penaltyRate.toRate(),
        )

        val initialState = InvestmentSimulationState(
            currentMonth = Month.ZERO,
            availableCash = initialCash.toMoney(),
            investments = emptyList(),
        )

        val decisions = listOf(
            listOf(InvestmentDecision.InvestAll(bondType)),
            listOf(InvestmentDecision.Withdraw),
        )

        val finalState = InvestmentSimulationRunner.replay(
            initialState,
            decisions,
            BigDecimal.ZERO.toMoney(),
        )

        val firstMonthRate = firstPeriodRate.divide("12".toBigDecimal(), 12, RoundingMode.HALF_UP)
        val accruedInterest = (initialCash * firstMonthRate).setScale(2, RoundingMode.HALF_UP)
        val payoutAfterPenalty =
            initialCash + accruedInterest - (initialCash * penaltyRate).setScale(2, RoundingMode.HALF_UP)
        val expectedValue = if (payoutAfterPenalty < initialCash) {
            initialCash
        } else {
            payoutAfterPenalty
        }.setScale(2, RoundingMode.HALF_UP)

        finalState.currentMonth shouldBe Month(2)
        finalState.totalValue().value.shouldBeCloseTo(expectedValue)
    }

    test("variable rate bond invest uses only full nominal units") {
        val initialCash = "1050".toBigDecimal()

        val bondType = VariableRateBondInvestmentType(
            firstPeriodRate = "0.044".toBigDecimal().toRate(),
            baseRate = "0.04".toBigDecimal().toRate(),
            margin = "0.0015".toBigDecimal().toRate(),
            durationMonths = Month(24),
            earlyRedemptionPenaltyRate = "0.007".toBigDecimal().toRate(),
        )

        val initialState = InvestmentSimulationState(
            currentMonth = Month.ZERO,
            availableCash = initialCash.toMoney(),
            investments = emptyList(),
        )

        val decisions = listOf(
            listOf(InvestmentDecision.InvestAll(bondType)),
        )

        val finalState = InvestmentSimulationRunner.replay(
            initialState,
            decisions,
            BigDecimal.ZERO.toMoney(),
        )

        val bondInvestment = finalState.investments.single() as VariableRateBondInvestment

        finalState.currentMonth shouldBe Month.ONE
        finalState.availableCash.value.shouldBeCloseTo("50.00".toBigDecimal())
        bondInvestment.principal.value.shouldBeCloseTo("1000.00".toBigDecimal())
    }

    test("immature bond total value includes early redemption penalty without withdraw") {
        val initialCash = "10000".toBigDecimal()
        val firstPeriodRate = "0.044".toBigDecimal()
        val baseRate = "0.04".toBigDecimal()
        val margin = "0.0015".toBigDecimal()
        val penaltyRate = "0.007".toBigDecimal()

        val bondType = VariableRateBondInvestmentType(
            firstPeriodRate = firstPeriodRate.toRate(),
            baseRate = baseRate.toRate(),
            margin = margin.toRate(),
            durationMonths = Month(24),
            earlyRedemptionPenaltyRate = penaltyRate.toRate(),
        )

        val initialState = InvestmentSimulationState(
            currentMonth = Month.ZERO,
            availableCash = initialCash.toMoney(),
            investments = emptyList(),
        )

        val decisions = listOf(
            listOf(InvestmentDecision.InvestAll(bondType)),
            listOf(InvestmentDecision.DoNothing),
        )

        val finalState = InvestmentSimulationRunner.replay(
            initialState,
            decisions,
            BigDecimal.ZERO.toMoney(),
        )

        val firstMonthRate = firstPeriodRate.divide("12".toBigDecimal(), 12, RoundingMode.HALF_UP)
        val secondMonthRate = (baseRate + margin).divide("12".toBigDecimal(), 12, RoundingMode.HALF_UP)

        val accruedInterest =
            (initialCash * firstMonthRate).setScale(2, RoundingMode.HALF_UP) +
                    (initialCash * secondMonthRate).setScale(2, RoundingMode.HALF_UP)
        val payoutAfterPenalty =
            initialCash + accruedInterest - (initialCash * penaltyRate).setScale(2, RoundingMode.HALF_UP)
        val expectedValue = if (payoutAfterPenalty < initialCash) initialCash else payoutAfterPenalty

        finalState.currentMonth shouldBe Month(2)
        finalState.totalValue().value.shouldBeCloseTo(expectedValue)
    }
})
