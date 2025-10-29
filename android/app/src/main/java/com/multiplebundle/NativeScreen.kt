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
            NativeScreen(
                onClickBiz1 = { navController.navigate("biz1") },
                onClickBiz2 = { navController.navigate("biz2") }
            )
        }
        composable("biz1") {
            ReactBizScreen(bundleName = "Biz1Bundle")
        }
        composable("biz2") {
            ReactBizScreen(bundleName = "Biz2Bundle")
        }
    }
}
