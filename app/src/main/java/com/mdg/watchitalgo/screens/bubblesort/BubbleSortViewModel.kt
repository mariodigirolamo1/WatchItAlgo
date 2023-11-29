package com.mdg.watchitalgo.screens.bubblesort

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mdg.watchitalgo.common.business.AlgorithmAutoPlayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

// TODO: isolate bubble sort steps
//  state of the algorithm stays here to have a coherent display in the ui but
//  logic for the algorithm needs to be placed elsewhere, this viewModel does too
//  many things
class BubbleSortViewModel: ViewModel() {
    private var _array = MutableStateFlow(unorderedStartingArray)
    val array: StateFlow<IntArray> = _array

    private var _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex

    private var _autoplaySpeed = MutableStateFlow(1f)
    val autoplaySpeed: StateFlow<Float> = _autoplaySpeed

    private var _isAutoPlaying = MutableStateFlow(false)
    val isAutoPlaying: StateFlow<Boolean> = _isAutoPlaying

    private var _sorted = MutableStateFlow(false)
    val sorted = _sorted

    private val bubbleSortStepMutex = Mutex()
    private var animateSwap = false
    private var swapped = true

    private val algorithmAutoPlayer = AlgorithmAutoPlayer()

    init{
        viewModelScope.launch {
            with(algorithmAutoPlayer) {
                _autoplaySpeed.emit(speed)
                _isAutoPlaying.emit(isAutoPlaying)
            }
        }
    }

    fun updateAutoplaySpeed(newSpeed: Float){
        viewModelScope.launch {
            algorithmAutoPlayer.speed = newSpeed
            _autoplaySpeed.emit(algorithmAutoPlayer.speed)
        }
    }

    fun restartAutoplayWithNewSpeed(){
        viewModelScope.launch(Dispatchers.Default) {
            algorithmAutoPlayer.restartAutoplayWithNewSpeed { timerTask() }
            _isAutoPlaying.emit(algorithmAutoPlayer.isAutoPlaying)
        }
    }

    fun resetAlgorithmState(){
        viewModelScope.launch(Dispatchers.Default) {
            bubbleSortStepMutex.withLock {
                withContext(Dispatchers.Main.immediate){
                    _array.emit(unorderedStartingArray)
                    _currentIndex.emit(0)
                    _sorted.emit(false)
                }
                animateSwap = false
                swapped = true
            }
            algorithmAutoPlayer.stop()
            _isAutoPlaying.emit(algorithmAutoPlayer.isAutoPlaying)
        }
    }

    fun toggleAutoPlay(){
        viewModelScope.launch {
            algorithmAutoPlayer.toggleAutoPlay { timerTask() }
            _isAutoPlaying.emit(algorithmAutoPlayer.isAutoPlaying)
        }
    }

    private fun timerTask(){
        viewModelScope.launch(Dispatchers.Default) {
            if(!_sorted.value){
                bubbleSortStep(manualStart = false)
            }else{
                algorithmAutoPlayer.stop()
                _isAutoPlaying.emit(algorithmAutoPlayer.isAutoPlaying)
            }
        }
    }

    fun bubbleSortStep(manualStart: Boolean) {
        viewModelScope.launch(Dispatchers.Default) {
            if(manualStart){
                algorithmAutoPlayer.stop()
                _isAutoPlaying.emit(algorithmAutoPlayer.isAutoPlaying)
            }
            bubbleSortStepMutex.withLock {
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

    private companion object{
        val unorderedStartingArray = intArrayOf(5,3,4,1,2)
    }
}