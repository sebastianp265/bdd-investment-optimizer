package com.github.sebastianp265.investment

import com.github.sebastianp265.investment.common.Money
import com.github.sebastianp265.investment.common.Month

data class PersonBoundPromotionalInvestment(
    override val principal: Money,
    val investmentMonth: Month,
    val template: PersonBoundPromotionalType,
) : Investment()

