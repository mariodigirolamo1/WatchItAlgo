package com.mdg.watchitalgo.algorithm.bubblesort

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class BubbleSortSolver{
    private val bubbleSortStepMutex = Mutex()

    suspend fun bubbleSortStep(
        array: IntArray,
        currentIndex: Int,
        animateSwap: Boolean,
        swapped: Boolean,
        sorted: Boolean
    ): BubbleSortState = runBlocking {
        var currentIndexOutput = currentIndex
        var animateSwapOutput = animateSwap
        var swappedOutput = swapped
        var sortedOutput = sorted

        if(!sorted) {
            bubbleSortStepMutex.withLock {
                if (!animateSwap) {
                    val didSwapInLastCycle = currentIndex == 0 && swapped
                    if (didSwapInLastCycle) {
                        swappedOutput = false
                    }
                    val isCurrentIndexInRange = currentIndex < array.size - 1
                    if (isCurrentIndexInRange) {
                        val isLeftHigherThenRight =
                            array[currentIndex] > array[currentIndex + 1]
                        if (isLeftHigherThenRight) {
                            swap(array = array, currentIndex = currentIndex)
                            swappedOutput = true
                            animateSwapOutput = true
                        } else {
                            currentIndexOutput++
                        }
                    } else {
                        if (!swapped) {
                            sortedOutput = true
                        } else {
                            currentIndexOutput = 0
                        }
                    }
                } else {
                    currentIndexOutput++
                    animateSwapOutput = false
                }
            }
        }

        BubbleSortState(
            array = array,
            currentIndex = currentIndexOutput,
            animateSwap = animateSwapOutput,
            swapped = swappedOutput,
            sorted = sortedOutput
        )
    }

    private fun swap(
        array: IntArray,
        currentIndex: Int
    ){
        val tmp = array[currentIndex+1]
        array[currentIndex+1] = array[currentIndex]
        array[currentIndex] = tmp
    }
}