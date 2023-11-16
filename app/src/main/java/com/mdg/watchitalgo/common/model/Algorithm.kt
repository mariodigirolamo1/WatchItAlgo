package com.mdg.watchitalgo.common.model

enum class Algorithm(
    val route: String,
    val available: Boolean
){
    BubbleSort(
        route = "bubblesort",
        available = true
    ),
    QuickSort(
        route = "quicksort",
        available = false
    ),
    InsertionSort(
        route = "insertionsort",
        available = false
    )
}