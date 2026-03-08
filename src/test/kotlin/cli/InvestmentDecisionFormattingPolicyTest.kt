package cli

import com.github.sebastianp265.cli.InvestmentDecisionFormattingPolicy
import com.github.sebastianp265.investment.common.Rate
import com.github.sebastianp265.investment.logic.InvestmentDecision
import com.github.sebastianp265.investment.model.type.FixedRateInvestmentType
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class InvestmentDecisionFormattingPolicyTest : FunSpec({

    test("formats InvestAll with investment type details") {
        val decision = InvestmentDecision.InvestAll(FixedRateInvestmentType(Rate("0.05".toBigDecimal())))

        val formatted = InvestmentDecisionFormattingPolicy.format(decision)

        formatted shouldBe "Invest all in Regular Account: 5% APR"
    }

    test("formats Withdraw") {
        val formatted = InvestmentDecisionFormattingPolicy.format(InvestmentDecision.Withdraw)

        formatted shouldBe "Withdraw all investments"
    }

    test("formats DoNothing") {
        val formatted = InvestmentDecisionFormattingPolicy.format(InvestmentDecision.DoNothing)

        formatted shouldBe "Do nothing"
    }
})
