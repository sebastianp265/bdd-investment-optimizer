package com.github.sebastianp265.graph

interface StateGraph<S, D> {

    fun possibleTransitions(state: S): List<Transition<S, D>>

}