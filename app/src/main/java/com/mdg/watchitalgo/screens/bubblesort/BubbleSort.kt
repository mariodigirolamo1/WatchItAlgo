package com.mdg.watchitalgo.screens.bubblesort

import android.widget.Space
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.viewModelFactory
import com.mdg.watchitalgo.R
import com.mdg.watchitalgo.common.model.Algorithm
import com.mdg.watchitalgo.common.ui.theme.Typography
import com.mdg.watchitalgo.common.ui.theme.WatchItAlgoTheme

@Composable
fun BubbleSort(
    bubbleSortViewModel: BubbleSortViewModel = hiltViewModel()
) {
    val array = bubbleSortViewModel.array.collectAsState()
    val currentIndex = bubbleSortViewModel.currentIndex.collectAsState()
    val sorted = bubbleSortViewModel.sorted.collectAsState()
    val autoplaySpeed = bubbleSortViewModel.autoplaySpeed.collectAsState()

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            BubbleSortLabel()
            Spacer(modifier = Modifier.size(80.dp))
            Bars(
                getArray = { array.value },
                getSorted = { sorted.value }
            ){ currentIndex.value }
            Spacer(modifier = Modifier.size(80.dp))
            AlgorithmControls(
                toggleAutoplay = { bubbleSortViewModel.toggleAutoPlay() },
                resetAlgorithmState = { bubbleSortViewModel.resetAlgorithmState() },
                getAutoplaySpeed = { autoplaySpeed.value },
                updateAutoplaySpeed = { newSpeed: Float ->
                    bubbleSortViewModel.updateAutoplaySpeed(newSpeed = newSpeed)
                },
                restartAutoplay = { bubbleSortViewModel.restartAutoplayWithNewSpeed() },
            ) { bubbleSortViewModel.bubbleSortStep(manualStart = true) }
        }
    }
}

@Composable
private fun BubbleSortLabel() {
    Text(
        text = Algorithm.BubbleSort.name,
        style = Typography.titleLarge,
        modifier = Modifier.padding(start = 8.dp)
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Bars(
    getArray: () -> IntArray,
    getSorted: () -> Boolean,
    getCurrentIndex: () -> Int
){
    LazyRow(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(.5f)
    ) {
        itemsIndexed(items = getArray().asList()){index: Int, value: Int ->
            val color: Color by animateColorAsState(
                targetValue = getBarColor(
                    itemIndex = index,
                    getSorted = getSorted,
                    getCurrentIndex = getCurrentIndex
                ),
                animationSpec = tween(400, easing = LinearEasing),
                label = "Bubble sort bars color animation"
            )
            Box(
                modifier = Modifier
                    .fillMaxHeight(.2f * value)
                    .widthIn(min = 60.dp)
                    .background(color)
                    .animateItemPlacement()
            ) {}
        }
    }
}

@Composable
private fun AlgorithmControls(
    toggleAutoplay: () -> Unit,
    resetAlgorithmState: () -> Unit,
    getAutoplaySpeed: () -> Float,
    updateAutoplaySpeed: (newSpeed: Float) -> Unit,
    restartAutoplay: () -> Unit,
    bubbleSortStep: () -> Unit
){
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement
                .spacedBy(
                    space = 8.dp,
                    alignment = Alignment.CenterHorizontally
                ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = resetAlgorithmState) {
                Text(text = stringResource(R.string.reset_button_text))
            }
            Button(onClick = bubbleSortStep) {
                Text(text = stringResource(R.string.next_button_text))
            }
            Button(onClick = toggleAutoplay) {
                Text(text = stringResource(R.string.autoplay_button_text))
            }
        }
        Spacer(modifier = Modifier.size(size = 30.dp))
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ){
            Text(text = "Autoplay speed")
        }
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ){
            Slider(
                value = getAutoplaySpeed(),
                onValueChange = updateAutoplaySpeed,
                onValueChangeFinished = restartAutoplay,
                valueRange = 0.1f .. 2f
            )
        }
    }
}

private fun getBarColor(
    itemIndex: Int,
    getSorted: () -> Boolean,
    getCurrentIndex: () -> Int
): Color {
    val currentIndex = getCurrentIndex()
    return if(!getSorted()) {
        when (itemIndex) {
            currentIndex -> {
                Color.Magenta
            }
            currentIndex + 1 -> {
                Color.Magenta
            }

            else -> {
                Color.DarkGray
            }
        }
    }else{
        Color.Green
    }
}

@Preview(showBackground = true)
@Composable
fun BubbleSortPreview() {
    WatchItAlgoTheme {
        BubbleSort()
    }
}