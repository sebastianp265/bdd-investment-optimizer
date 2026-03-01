package com.github.sebastianp265.graph

data class Transition<S, D>(
    val decisions: List<D>,
    val nextState: S,
)