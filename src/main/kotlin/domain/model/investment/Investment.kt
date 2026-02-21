package com.github.sebastianp265.domain.model.investment

import com.github.sebastianp265.domain.snapshot.InvestmentSnapshot

sealed interface Investment {
    fun snapshot(): InvestmentSnapshot
}