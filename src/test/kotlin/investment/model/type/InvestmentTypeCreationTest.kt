package investment.model.type

import com.github.sebastianp265.investment.common.Money
import com.github.sebastianp265.investment.common.Month
import com.github.sebastianp265.investment.common.Rate
import com.github.sebastianp265.investment.model.FixedRateInvestment
import com.github.sebastianp265.investment.model.PersonBoundPromotionalInvestment
import com.github.sebastianp265.investment.model.VariableRateBondInvestment
import com.github.sebastianp265.investment.model.type.FixedRateInvestmentType
import com.github.sebastianp265.investment.model.type.PersonBoundPromotionalInvestmentType
import com.github.sebastianp265.investment.model.type.VariableRateBondInvestmentType
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.math.BigDecimal

class InvestmentTypeCreationTest : FunSpec({

    test("create fixed rate investment uses all cash") {
        val availableCash = Money("1000.00".toBigDecimal())
        val type = FixedRateInvestmentType(Rate("0.05".toBigDecimal()))

        val created = type.createInvestment(availableCash, Month(2))

        (created.investment as FixedRateInvestment).principal.value shouldBe "1000.00".toBigDecimal()
        created.remainingCash.value shouldBe BigDecimal.ZERO
    }

    test("create promotional investment uses all cash") {
        val availableCash = Money("1500.00".toBigDecimal())
        val type = PersonBoundPromotionalInvestmentType(
            rate = Rate("0.05".toBigDecimal()),
            promotionalRate = Rate("0.10".toBigDecimal()),
            promotionDurationMonths = Month(3),
        )

        val created = type.createInvestment(availableCash, Month(1))

        (created.investment as PersonBoundPromotionalInvestment).principal.value shouldBe "1500.00".toBigDecimal()
        created.remainingCash.value shouldBe BigDecimal.ZERO
    }

    test("create bond investment leaves non-nominal remainder in cash") {
        val availableCash = Money("1050.00".toBigDecimal())
        val type = VariableRateBondInvestmentType(
            firstPeriodRate = Rate("0.044".toBigDecimal()),
            baseRate = Rate("0.04".toBigDecimal()),
            margin = Rate("0.0015".toBigDecimal()),
            durationMonths = Month(24),
            earlyRedemptionPenaltyRate = Rate("0.007".toBigDecimal()),
        )

        val created = type.createInvestment(availableCash, Month.ZERO)

        (created.investment as VariableRateBondInvestment).principal.value shouldBe "1000.00".toBigDecimal()
        created.remainingCash.value shouldBe "50.00".toBigDecimal()
    }
})

