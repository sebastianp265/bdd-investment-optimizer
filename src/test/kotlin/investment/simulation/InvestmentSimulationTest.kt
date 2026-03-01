package investment.simulation

import com.github.sebastianp265.investment.common.Money
import com.github.sebastianp265.investment.common.Month
import com.github.sebastianp265.investment.common.Rate
import com.github.sebastianp265.investment.model.FixedRateType
import com.github.sebastianp265.investment.model.InvestmentDecision
import com.github.sebastianp265.investment.simulation.InvestmentSimulation
import com.github.sebastianp265.investment.state.InvestmentSimulationState
import com.github.sebastianp265.optimizer.DFSOptimizationEngine
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.types.shouldBeInstanceOf
import java.math.BigDecimal
import java.math.RoundingMode

class InvestmentSimulationTest : FunSpec({

    test("optimization finds best investment strategy with monthly deposits") {
        val initialCash = Money(BigDecimal("20000.00"))
        val monthlyDeposit = Money(BigDecimal("1000.00"))
        val rate = BigDecimal("0.05")
        val savingsAccountTemplate = FixedRateType(Rate(rate))

        val simulation = InvestmentSimulation(
            finalMonth = Month(3),
            availableAccounts = listOf(savingsAccountTemplate),
            monthlyDeposit = monthlyDeposit,
        )

        val initialState = InvestmentSimulationState(
            currentMonth = Month.Companion.ZERO,
            availableCash = initialCash,
            investments = emptyList()
        )

        val monthlyRate = rate.divide(BigDecimal("12"), 12, RoundingMode.HALF_UP)
        val inv1 = BigDecimal("20000.00") * BigDecimal.ONE.add(monthlyRate).pow(3)
        val inv2 = BigDecimal("1000.00") * BigDecimal.ONE.add(monthlyRate).pow(2)
        val inv3 = BigDecimal("1000.00") * BigDecimal.ONE.add(monthlyRate).pow(1)
        val cash = BigDecimal("1000.00")

        val (bestState, decisions) = DFSOptimizationEngine.optimize(simulation, initialState)

        val expectedValue = (inv1 + inv2 + inv3 + cash).setScale(2, RoundingMode.HALF_UP)

        bestState.currentMonth shouldBeEqual Month(3)
        bestState.totalValue().value shouldBeEqual expectedValue

        decisions.size shouldBeEqual 3
        decisions[0][0].shouldBeInstanceOf<InvestmentDecision.Invest>()
        decisions[1][0].shouldBeInstanceOf<InvestmentDecision.Invest>()
        decisions[2][0].shouldBeInstanceOf<InvestmentDecision.Invest>()
    }

})