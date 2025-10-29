package com.multiplebundle

import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import androidx.navigation.NavHostController
import com.facebook.react.ReactActivityDelegate
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint.fabricEnabled


@Composable
fun AppNavGraph(navController: NavHostController = rememberNavController()) {

    NavHost(navController = navController, startDestination = "native") {
        composable("native") {
            NativeScreen(onClick = { navController.navigate("biz") })
        }
        composable("biz") {
            ReactBizScreen()
        }
    }
}
