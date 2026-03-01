package com.github.sebastianp265.graph

interface Action {
    // should return null if action doesn't make sense
    fun apply(state: State): State?
}