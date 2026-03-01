package com.github.sebastianp265.investment

import com.github.sebastianp265.investment.common.Money

sealed class Investment {
    abstract fun currentValue(): Money
}

