package com.github.sebastianp265.optimizer

import com.github.sebastianp265.graph.StateGraph

interface OptimizationEngine {

    fun <S : Comparable<S>, D> optimize(
        stateGraph: StateGraph<S, D>,
        initialState: S,
    ): Pair<S, List<List<D>>>

}