package com.mdg.watchitalgo.screens.bubblesort

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mdg.watchitalgo.algorithm.bubblesort.BubbleSortSolver
import com.mdg.watchitalgo.algorithm.bubblesort.BubbleSortState
import com.mdg.watchitalgo.common.business.AlgorithmAutoPlayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random
import kotlin.random.nextInt

class BubbleSortViewModel: ViewModel() {
    // TODO: this needs to be some autoplay state
    private var _autoplaySpeed = MutableStateFlow(1f)
    val autoplaySpeed: StateFlow<Float> = _autoplaySpeed

    private var _isAutoPlaying = MutableStateFlow(false)
    val isAutoPlaying: StateFlow<Boolean> = _isAutoPlaying

    private val _bubbleSortState = MutableStateFlow(startingBubbleSortState)
    val bubbleSortState: StateFlow<BubbleSortState> = _bubbleSortState

    private val bubbleSortSolver = BubbleSortSolver()
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
        val newArray = IntArray(5){ Random.nextInt(1 .. 5) }
        // TODO: remove those magic values
        val newState = BubbleSortState(
            array = newArray,
            currentIndex = 0,
            animateSwap = false,
            swapped = false,
            sorted = false
        )
        viewModelScope.launch {
            _bubbleSortState.emit(newState)
            withContext(Dispatchers.Default){
                stopAlgorithmAutoPlayer()
            }
        }
    }

    fun toggleAutoPlay(){
        viewModelScope.launch {
            algorithmAutoPlayer.toggleAutoPlay { timerTask() }
            _isAutoPlaying.emit(algorithmAutoPlayer.isAutoPlaying)
        }
    }

    // TODO: name too generic, change this
    private fun timerTask(){
        viewModelScope.launch(Dispatchers.Default) {
            if(!_bubbleSortState.value.sorted){
                bubbleSortStep(manualStart = false)
            }else{
                stopAlgorithmAutoPlayer()
            }
        }
    }

    fun bubbleSortStep(manualStart: Boolean) {
        viewModelScope.launch(Dispatchers.Default) {
            if(manualStart){
                stopAlgorithmAutoPlayer()
            }

            // TODO: this could probably be avoided/simplified
            val tmpArray = IntArray(_bubbleSortState.value.array.size){0}
            _bubbleSortState.value.array.forEachIndexed {index, value ->
                tmpArray[index] = value
            }
            val tmpCurrentIndex = _bubbleSortState.value.currentIndex

            val newBubbleSortState = bubbleSortSolver.bubbleSortStep(
                array = tmpArray,
                currentIndex = tmpCurrentIndex,
                animateSwap = _bubbleSortState.value.animateSwap,
                swapped = _bubbleSortState.value.swapped,
                sorted = _bubbleSortState.value.sorted
            )

            viewModelScope.launch {
                _bubbleSortState.emit(newBubbleSortState)
            }
        }
    }

    private suspend fun stopAlgorithmAutoPlayer(){
        algorithmAutoPlayer.stop()
        _isAutoPlaying.emit(algorithmAutoPlayer.isAutoPlaying)
    }

    private companion object{
        // TODO: always use a random one
        val unorderedStartingArray = intArrayOf(5,3,4,1,2)

        val startingBubbleSortState = BubbleSortState(
            array = unorderedStartingArray,
            currentIndex = 0,
            animateSwap = false,
            swapped = false,
            sorted = false
        )
    }
}