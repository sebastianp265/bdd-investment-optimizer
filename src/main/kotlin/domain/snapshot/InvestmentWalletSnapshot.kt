package com.github.sebastianp265.domain.snapshot

import com.github.sebastianp265.domain.model.InvestmentId

data class InvestmentWalletSnapshot(
    val investments: Map<InvestmentId, InvestmentSnapshot>,
)