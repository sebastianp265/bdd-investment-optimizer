package com.github.sebastianp265.graph

data class Transition<S, A>(
    val actions: List<A>,
    val nextState: S
)