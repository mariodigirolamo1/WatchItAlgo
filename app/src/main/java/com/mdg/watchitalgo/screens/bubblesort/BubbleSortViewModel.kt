package com.mdg.watchitalgo.screens.bubblesort

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.util.Timer
import kotlin.concurrent.timer

class BubbleSortViewModel: ViewModel() {
    private var _array = MutableStateFlow(unorderedStartingArray)
    val array: StateFlow<IntArray> = _array

    private var _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex

    private var _sorted = MutableStateFlow(false)
    val sorted = _sorted

    private var _autoplaySpeed = MutableStateFlow(1f)
    val autoplaySpeed: StateFlow<Float> = _autoplaySpeed

    private val bubbleSortStepMutex = Mutex()
    private var animateSwap = false
    private var swapped = true

    // TODO: all this autoplay logic does not belong here
    //  will be held in new package/module for shared timer logic
    private val autoplayMutex = Mutex()
    private var _isAutoPlaying = MutableStateFlow(false)
    val isAutoPlaying: StateFlow<Boolean> = _isAutoPlaying
    private var autoplayTimer: Timer? = null

    fun updateAutoplaySpeed(newSpeed: Float){
        viewModelScope.launch(Dispatchers.Default) {
            autoplayMutex.withLock {
                withContext(Dispatchers.Main.immediate){
                    _autoplaySpeed.emit(newSpeed)
                }
            }
        }
    }

    fun restartAutoplayWithNewSpeed(){
        viewModelScope.launch(Dispatchers.Default) {
            autoplayMutex.withLock {
                autoplayTimer?.cancel()
                if(_isAutoPlaying.value){
                    val period = (1000L / autoplaySpeed.value).toLong()
                    autoplayTimer = timer(
                        name = "autoplay timer",
                        initialDelay = 0,
                        daemon = false,
                        period = period
                    ){ timerTask() }
                }else{
                    _isAutoPlaying.emit(false)
                }
            }
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
            stopAutoplay()
        }
    }

    fun toggleAutoPlay() = runBlocking{
        withContext(Dispatchers.Default){
            autoplayMutex.withLock {
                val period = (1000L / autoplaySpeed.value).toLong()
                if(_isAutoPlaying.value){
                    _isAutoPlaying.emit(false)
                    autoplayTimer?.cancel()
                }else{
                    _isAutoPlaying.emit(true)
                    autoplayTimer = timer(
                        name = "autoplay timer",
                        initialDelay = 0,
                        daemon = false,
                        period = period
                    ){ timerTask() }
                }
            }
        }
    }

    private fun timerTask(){
        viewModelScope.launch(Dispatchers.Default) {
            if(!_sorted.value){
                bubbleSortStep(manualStart = false)
            }else{
                stopAutoplay()
            }
        }
    }

    fun bubbleSortStep(manualStart: Boolean) = runBlocking{
        viewModelScope.launch(Dispatchers.Default) {
            if(manualStart){ stopAutoplay() }
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

    private suspend fun stopAutoplay(){
        autoplayMutex.withLock {
            autoplayTimer?.cancel()
            _isAutoPlaying.emit(false)
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