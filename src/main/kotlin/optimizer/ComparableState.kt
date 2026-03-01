package com.github.sebastianp265.optimizer

interface ComparableState<V : Comparable<V>> : Comparable<ComparableState<V>> {

    fun value(): V

    override fun compareTo(other: ComparableState<V>): Int {
        return value().compareTo(other.value())
    }
}