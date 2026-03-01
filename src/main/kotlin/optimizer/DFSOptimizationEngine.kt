package com.github.sebastianp265.optimizer

import com.github.sebastianp265.graph.StateGraph

object DFSOptimizationEngine : OptimizationEngine {

    override fun <S : Comparable<S>, A> optimize(
        stateGraph: StateGraph<S, A>,
        initialState: S
    ): Pair<S, List<List<A>>> {
        var bestState = initialState
        var bestSequenceOfTransitions: List<List<A>> = emptyList()

        fun dfs(state: S, history: List<List<A>>) {
            if (stateGraph.isTerminal(state)) {
                if (state > bestState) {
                    bestState = state
                    bestSequenceOfTransitions = history
                }
                return
            }

            for (transition in stateGraph.possibleTransitions(state)) {
                dfs(
                    transition.nextState,
                    history + listOf(transition.actions)
                )
            }
        }

        dfs(initialState, emptyList())

        return bestState to bestSequenceOfTransitions
    }

}