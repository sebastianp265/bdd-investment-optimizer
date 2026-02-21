package com.github.sebastianp265.domain.value

@JvmInline
value class Month(val index: Int) : Comparable<Month> {

    companion object {
        val ZERO = Month(0)
    }

    init {
        require(index >= 0)
    }

    operator fun inc() = Month(index + 1)
    override operator fun compareTo(other: Month): Int = this.index.compareTo(other.index)

    fun monthsSince(other: Month) = Month(index - other.index)
    fun monthsAfter(other: Month) = Month(index + other.index)
}