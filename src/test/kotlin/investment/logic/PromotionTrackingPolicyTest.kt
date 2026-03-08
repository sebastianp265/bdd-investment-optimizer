package investment.logic

import com.github.sebastianp265.investment.common.Month
import com.github.sebastianp265.investment.common.Rate
import com.github.sebastianp265.investment.logic.PromotionTrackingPolicy
import com.github.sebastianp265.investment.model.type.FixedRateInvestmentType
import com.github.sebastianp265.investment.model.type.PersonBoundPromotionalInvestmentType
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class PromotionTrackingPolicyTest : FunSpec({

    test("updateAfterInvestAll adds promo start month only once") {
        val promoType = PersonBoundPromotionalInvestmentType(
            rate = Rate("0.05".toBigDecimal()),
            promotionalRate = Rate("0.10".toBigDecimal()),
            promotionDurationMonths = Month(3),
        )

        val first = PromotionTrackingPolicy.updateAfterInvestAll(
            current = emptyMap(),
            investmentType = promoType,
            currentMonth = Month(2),
        )
        val second = PromotionTrackingPolicy.updateAfterInvestAll(
            current = first,
            investmentType = promoType,
            currentMonth = Month(5),
        )

        first[promoType] shouldBe Month(2)
        second[promoType] shouldBe Month(2)
    }

    test("updateAfterInvestAll ignores non-promotional investments") {
        val fixedType = FixedRateInvestmentType(Rate("0.05".toBigDecimal()))

        val updated = PromotionTrackingPolicy.updateAfterInvestAll(
            current = emptyMap(),
            investmentType = fixedType,
            currentMonth = Month(1),
        )

        updated shouldBe emptyMap()
    }
})

