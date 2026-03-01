package com.github.sebastianp265.optimizer

import com.github.sebastianp265.graph.StateGraph

object DFSOptimizationEngine : OptimizationEngine {

    override fun <S : Comparable<S>, D> optimize(
        stateGraph: StateGraph<S, D>,
        initialState: S
    ): Pair<S, List<List<D>>> {
        var bestState = initialState
        var bestSequenceOfTransitions: List<List<D>> = emptyList()

        fun dfs(state: S, history: List<List<D>>) {
            val transitions = stateGraph.possibleTransitions(state)

            if (transitions.isEmpty()) {
                if (state > bestState) {
                    bestState = state
                    bestSequenceOfTransitions = history
                }
                return
            }

            for (transition in transitions) {
                dfs(
                    transition.nextState,
                    history + listOf(transition.decisions)
                )
            }
        }

        dfs(initialState, emptyList())

        return bestState to bestSequenceOfTransitions
    }

}