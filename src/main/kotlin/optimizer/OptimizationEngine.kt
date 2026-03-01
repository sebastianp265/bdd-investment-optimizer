package com.github.sebastianp265.optimizer

import com.github.sebastianp265.graph.StateGraph

interface OptimizationEngine {

    fun <S: Comparable<S>, A> optimize(
        stateGraph: StateGraph<S, A>,
        initialState: S,
    ): Pair<S, List<List<A>>>

}