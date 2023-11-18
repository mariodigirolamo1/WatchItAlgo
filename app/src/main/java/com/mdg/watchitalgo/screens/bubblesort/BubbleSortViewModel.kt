package com.mdg.watchitalgo.screens.bubblesort

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BubbleSortViewModel: ViewModel() {
    private var _array = MutableStateFlow(intArrayOf(5,3,4,1,2))
    val array = _array

    private var _currentIndex = MutableStateFlow(0)
    val currentIndex = _currentIndex

    private var _sorted = MutableStateFlow(false)
    val sorted = _sorted

    private var animateSwap = false
    private var swapped = true

    fun bubbleSortStep(){
        viewModelScope.launch(Dispatchers.Default) {
            val tmpArray = IntArray(_array.value.size){0}
            _array.value.forEachIndexed {index, value ->
                tmpArray[index] = value
            }
            val tmpCurrentIndex = currentIndex.value

            if(!animateSwap){
                if(tmpCurrentIndex == 0 && swapped){
                    swapped = false
                }
                if(
                    tmpCurrentIndex < tmpArray.size - 1
                ){
                    if(tmpArray[tmpCurrentIndex] > tmpArray[tmpCurrentIndex+1]){
                        doSwap(tmpArray = tmpArray, tmpCurrentIndex = tmpCurrentIndex)
                    }else{
                        withContext(Dispatchers.Main.immediate){
                            _currentIndex.emit(_currentIndex.value+1)
                        }
                    }
                }else{
                    if(!swapped){
                        withContext(Dispatchers.Main.immediate){
                            _sorted.emit(true)
                        }
                    }else{
                        withContext(Dispatchers.Main.immediate){
                            _currentIndex.emit(0)
                        }
                    }
                }
                withContext(Dispatchers.Main.immediate){
                    _array.emit(tmpArray)
                }
            }else{
                triggerColorAnimation()
            }
        }
    }

    private fun doSwap(
        tmpArray: IntArray,
        tmpCurrentIndex: Int
    ){
        val tmp = tmpArray[tmpCurrentIndex+1]
        tmpArray[tmpCurrentIndex+1] = tmpArray[tmpCurrentIndex]
        tmpArray[tmpCurrentIndex] = tmp
        swapped = true
        animateSwap = true
    }

    private suspend fun triggerColorAnimation(){
        withContext(Dispatchers.Main.immediate){
            _currentIndex.emit(_currentIndex.value+1)
        }
        animateSwap = false
    }
}