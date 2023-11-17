package com.mdg.watchitalgo

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mdg.watchitalgo.common.model.Algorithm
import com.mdg.watchitalgo.screens.bubblesort.BubbleSort
import com.mdg.watchitalgo.screens.home.Home

/**
 * It contains an home page which allows you to navigate to different algorithms screens.
 */
@Composable
fun WatchItAlgoApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home"){
        composable("home"){
            Home{route ->
                navController.navigate(route = route)
            }
        }
        composable(Algorithm.BubbleSort.route){
            BubbleSort()
        }
    }
}