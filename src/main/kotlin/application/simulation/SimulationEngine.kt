package com.github.sebastianp265.application.simulation

import com.github.sebastianp265.domain.snapshot.InvestmentWalletSnapshot

interface SimulationEngine {
    fun step()
    fun run(months: Int): InvestmentWalletSnapshot
}



