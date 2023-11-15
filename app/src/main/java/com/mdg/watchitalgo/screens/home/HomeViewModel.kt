package com.mdg.watchitalgo.screens.home

import androidx.lifecycle.ViewModel
import com.mdg.watchitalgo.common.model.Algorithm
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.LinkedList

class HomeViewModel: ViewModel(){
    private val _algorithms = MutableStateFlow(
        LinkedList(
            listOf(
                Algorithm("Bubble sort",true),
                Algorithm("Quicksort",false),
                Algorithm("Insertion sort",false)
            )
        )
    )
    var algorithms = _algorithms
}