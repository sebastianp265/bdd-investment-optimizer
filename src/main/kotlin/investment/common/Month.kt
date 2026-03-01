package com.github.sebastianp265.investment.common

@JvmInline
value class Month(val index: Int) : Comparable<Month> {

    companion object {
        val ZERO = Month(0)
        val ONE = Month(1)
    }

    init {
        require(index >= 0)
    }

    operator fun plus(other: Month) = Month(index + other.index)
    operator fun minus(other: Month) = Month(index - other.index)
    override operator fun compareTo(other: Month): Int = this.index.compareTo(other.index)
}