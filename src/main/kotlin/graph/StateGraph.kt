package com.github.sebastianp265.graph

interface StateGraph<S, A> {

    fun isTerminal(state: S): Boolean

    fun possibleTransitions(state: S): List<Transition<S, A>>

}