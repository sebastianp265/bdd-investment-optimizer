package optimizer

import com.github.sebastianp265.investment.common.Money
import com.github.sebastianp265.investment.common.Month
import com.github.sebastianp265.investment.common.toMoney
import com.github.sebastianp265.investment.common.toRate
import com.github.sebastianp265.investment.logic.InvestmentDecision
import com.github.sebastianp265.investment.model.type.FixedRateInvestmentType
import com.github.sebastianp265.investment.model.type.PersonBoundPromotionalInvestmentType
import com.github.sebastianp265.investment.simulation.InvestmentSimulation
import com.github.sebastianp265.investment.state.InvestmentSimulationState
import com.github.sebastianp265.optimizer.DFSOptimizationEngine
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import shouldBeCloseTo
import java.math.BigDecimal
import java.math.RoundingMode

class DFSOptimizationEngineInvestmentTest : FunSpec({

    test("dfs optimization chooses regular account then promotional for maximum returns") {
        val initialCash = "10000".toBigDecimal()
        val fixedTypeRate = "0.02".toBigDecimal()
        val promotionalNormalRate = "0.01".toBigDecimal()
        val promotionalRate = "0.05".toBigDecimal()

        val fixedRateInvestmentType = FixedRateInvestmentType(fixedTypeRate.toRate())
        val personBoundPromotionalInvestmentType = PersonBoundPromotionalInvestmentType(
            rate = promotionalNormalRate.toRate(),
            promotionalRate = promotionalRate.toRate(),
            promotionDurationMonths = Month(3),
        )

        val simulation = InvestmentSimulation(
            finalMonth = Month(6),
            availableInvestmentTypes = listOf(fixedRateInvestmentType, personBoundPromotionalInvestmentType),
            monthlyDeposit = Money.ZERO,
        )

        val initialState = InvestmentSimulationState(
            currentMonth = Month.ZERO,
            availableCash = initialCash.toMoney(),
            investments = emptyList(),
        )

        val (bestState, decisions) = DFSOptimizationEngine.optimize(simulation, initialState)

        bestState.currentMonth.index shouldBe 6
        decisions shouldBe listOf(
            listOf(InvestmentDecision.InvestAll(fixedRateInvestmentType)),
            listOf(InvestmentDecision.DoNothing),
            listOf(InvestmentDecision.DoNothing),
            listOf(
                InvestmentDecision.Withdraw,
                InvestmentDecision.InvestAll(personBoundPromotionalInvestmentType),
            ),
            listOf(InvestmentDecision.DoNothing),
            listOf(InvestmentDecision.DoNothing),
        )

        val fixedTypeMonthlyRate = fixedTypeRate.divide(BigDecimal("12"), 12, RoundingMode.HALF_UP)
        val promotionalMonthlyRate = promotionalRate.divide(BigDecimal("12"), 12, RoundingMode.HALF_UP)

        val inv1 = initialCash * (BigDecimal.ONE + fixedTypeMonthlyRate).pow(3)
        val inv2 = inv1 * (BigDecimal.ONE + promotionalMonthlyRate).pow(3)

        val expectedValue = inv2.setScale(2, RoundingMode.HALF_UP)

        bestState.totalLiquidationValue().value.shouldBeCloseTo(expectedValue)
    }


})