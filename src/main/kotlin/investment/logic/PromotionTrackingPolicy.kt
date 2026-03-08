package com.github.sebastianp265.investment.logic

import com.github.sebastianp265.investment.common.Month
import com.github.sebastianp265.investment.model.type.InvestmentType
import com.github.sebastianp265.investment.model.type.PersonBoundPromotionalInvestmentType

object PromotionTrackingPolicy {

    fun updateAfterInvestAll(
        current: Map<InvestmentType, Month>,
        investmentType: InvestmentType,
        currentMonth: Month,
    ): Map<InvestmentType, Month> {
        if (investmentType !is PersonBoundPromotionalInvestmentType) return current
        if (current.containsKey(investmentType)) return current
        return current + (investmentType to currentMonth)
    }

}

