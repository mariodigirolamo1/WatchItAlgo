package com.mdg.watchitalgo.common.business

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.util.Timer
import kotlin.concurrent.timer

class AlgorithmAutoPlayer {
    private val autoplayMutex = Mutex()
    private var autoplayTimer: Timer? = null

    var isAutoPlaying = false
        get() = runBlocking {
            // TODO: inject dispatcher for testing
            withContext(Dispatchers.Default){
                autoplayMutex.withLock {
                    field
                }
            }
        }
        set(value) = runBlocking {
            // TODO: inject dispatcher for testing
            withContext(Dispatchers.Default){
                autoplayMutex.withLock {
                    field = value
                }
            }
        }
    var speed = 1.0f
        get() = runBlocking {
            // TODO: inject dispatcher for testing
            withContext(Dispatchers.Default){
                autoplayMutex.withLock {
                    field
                }
            }
        }
        set(value) = runBlocking {
            // TODO: inject dispatcher for testing
            withContext(Dispatchers.Default){
                autoplayMutex.withLock {
                    field = value
                }
            }
        }

    suspend fun stop() {
        withContext(Dispatchers.Default) {
            autoplayTimer?.cancel()
            isAutoPlaying = false
        }
    }

    suspend fun restartAutoplayWithNewSpeed(
        // todo: find a better name
        action: () -> Unit
    ) {
        withContext(Dispatchers.Default){
            autoplayTimer?.cancel()
            if(isAutoPlaying){
                val period = (1000L / speed).toLong()
                autoplayTimer = timer(
                    // TODO: remove those magic values
                    name = "autoplay timer",
                    initialDelay = 0,
                    daemon = false,
                    period = period
                ){ action() }
            }
        }
    }

    // TODO: refactor accordingly to restartAutoplayWithNewSpeed
    suspend fun toggleAutoPlay(
        action: () -> Unit
    ) {
        withContext(Dispatchers.Default){
            val period = (1000L / speed).toLong()
            if(isAutoPlaying){
                autoplayTimer?.cancel()
            }else{
                autoplayTimer = timer(
                    name = "autoplay timer",
                    initialDelay = 0,
                    daemon = false,
                    period = period
                ){ action() }
            }
            isAutoPlaying = !isAutoPlaying
        }
    }
}