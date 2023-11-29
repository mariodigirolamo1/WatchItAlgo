package com.mdg.watchitalgo.algorithm.bubblesort

data class BubbleSortState (
    val array: IntArray,
    val currentIndex: Int,
    val animateSwap: Boolean,
    val swapped: Boolean,
    val sorted: Boolean
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BubbleSortState

        if (!array.contentEquals(other.array)) return false
        if (currentIndex != other.currentIndex) return false
        if (animateSwap != other.animateSwap) return false
        if (swapped != other.swapped) return false
        if (sorted != other.sorted) return false

        return true
    }

    override fun hashCode(): Int {
        var result = array.contentHashCode()
        result = 31 * result + currentIndex
        result = 31 * result + animateSwap.hashCode()
        result = 31 * result + swapped.hashCode()
        result = 31 * result + sorted.hashCode()
        return result
    }
}