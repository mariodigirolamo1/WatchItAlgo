package com.mdg.watchitalgo.screens.home

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Card
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mdg.watchitalgo.common.model.Algorithm
import com.mdg.watchitalgo.common.ui.theme.Typography
import com.mdg.watchitalgo.common.ui.theme.WatchItAlgoTheme

@Composable
fun Home(
    navigateToAlgorithmScreen: (route: String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 8.dp)
    ) {
        Column {
            AlgoListLabel()
            Spacer(modifier = Modifier.size(size = 8.dp))
            AlgoList(
                navigateToAlgorithmScreen = navigateToAlgorithmScreen
            )
        }
    }
}

@Composable
fun AlgoListLabel() {
    Text(
        text = "Algorithms",
        style = Typography.titleLarge,
        modifier = Modifier.padding(start = 8.dp)
    )
}

@Composable
private fun AlgoList(
    navigateToAlgorithmScreen: (route: String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(count = 2),
        modifier = Modifier.fillMaxSize()
    ) {
        Algorithm.values().forEach {algorithm ->
            item {
                AlgorithmCard(algorithm = algorithm){route ->
                    navigateToAlgorithmScreen(route)
                }
            }
        }
    }
}

@Composable
fun AlgorithmCard(
    algorithm: Algorithm,
    navigateToAlgorithmScreen: (route: String) -> Unit
) {
    val alpha = if(algorithm.available){ 1f }else{ 0.35f }
    val context = LocalContext.current
    val toast = createToast(algorithm = algorithm, context = context)
    val onClick: () -> Unit = if(algorithm.available) {
        {
            navigateToAlgorithmScreen(algorithm.route)
        }
    } else {
        {
            toast.show()
        }
    }
    Card(
        shape = RectangleShape,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(all = 8.dp)
            .alpha(alpha = alpha)
            .clickable { onClick() }
    ) {
        Column(
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .height(height = 156.dp)
                .padding(all = 8.dp)
        ) {
            Text(
                text = algorithm.name,
                style = Typography.titleMedium,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

private fun createToast(
    algorithm: Algorithm,
    context: Context
): Toast {
    return if(algorithm.available){
        Toast.makeText(
            context,
            "Navigating to ${algorithm.name}",
            Toast.LENGTH_SHORT
        )
    }else{
        Toast.makeText(
            context,
            "Algorithm unavailable",
            Toast.LENGTH_SHORT
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    WatchItAlgoTheme {
        Home{}
    }
}