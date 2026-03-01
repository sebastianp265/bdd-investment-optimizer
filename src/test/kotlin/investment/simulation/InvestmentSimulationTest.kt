package investment.simulation

import com.github.sebastianp265.investment.common.Money
import com.github.sebastianp265.investment.common.Month
import com.github.sebastianp265.investment.common.Rate
import com.github.sebastianp265.investment.model.FixedRateType
import com.github.sebastianp265.investment.model.InvestmentDecision
import com.github.sebastianp265.investment.model.PersonBoundPromotionalType
import com.github.sebastianp265.investment.simulation.InvestmentSimulation
import com.github.sebastianp265.investment.state.InvestmentSimulationState
import com.github.sebastianp265.optimizer.DFSOptimizationEngine
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.bigdecimal.shouldBeLessThanOrEqual
import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import java.math.RoundingMode

class InvestmentSimulationTest : FunSpec({

    test("dfs optimization chooses regular account then promotional for maximum returns") {
        val initialCash = Money(BigDecimal("10000.00"))

        val regularAccountTemplate = FixedRateType(Rate(BigDecimal("0.02")))
        val promotionalAccountTemplate = PersonBoundPromotionalType(
            rate = Rate(BigDecimal("0.01")),
            promotionalRate = Rate(BigDecimal("0.05")),
            promotionDurationMonths = Month(3),
        )

        val simulation = InvestmentSimulation(
            finalMonth = Month(6),
            availableInvestmentTypes = listOf(regularAccountTemplate, promotionalAccountTemplate),
            monthlyDeposit = Money.ZERO,
        )

        val initialState = InvestmentSimulationState(
            currentMonth = Month.ZERO,
            availableCash = initialCash,
            investments = emptyList(),
        )

        val (bestState, decisions) = DFSOptimizationEngine.optimize(simulation, initialState)

        val regularMonthlyRate = BigDecimal("0.02").divide(BigDecimal("12"), 12, RoundingMode.HALF_UP)
        val promotionalMonthlyRate = BigDecimal("0.05").divide(BigDecimal("12"), 12, RoundingMode.HALF_UP)

        val valueAfter3MonthsRegular = BigDecimal("10000.00") * BigDecimal.ONE.add(regularMonthlyRate).pow(3)
        val finalValue = valueAfter3MonthsRegular * BigDecimal.ONE.add(promotionalMonthlyRate).pow(3)

        val expectedValue = finalValue.setScale(2, RoundingMode.HALF_UP)

        bestState.currentMonth shouldBe Month(6)

        // TODO: To fix this: this should be precisely calculated, but currently logic handles savings account as separate investments, but they should be shared
        (bestState.totalValue().value - expectedValue).abs() shouldBeLessThanOrEqual BigDecimal("0.01")
        decisions shouldBe listOf(
            listOf(InvestmentDecision.Invest(regularAccountTemplate, initialCash)),
            listOf(InvestmentDecision.DoNothing),
            listOf(InvestmentDecision.DoNothing),
            listOf(
                InvestmentDecision.Withdraw,
                InvestmentDecision.Invest(
                    promotionalAccountTemplate,
                    Money(valueAfter3MonthsRegular.setScale(2, RoundingMode.HALF_UP))
                ),
            ),
            listOf(InvestmentDecision.DoNothing),
            listOf(InvestmentDecision.DoNothing),
        )
    }

})
