package com.github.sebastianp265.domain.snapshot

import com.github.sebastianp265.domain.value.Money

data class AccountSnapshot(
    val balance: Money,
) : InvestmentSnapshot