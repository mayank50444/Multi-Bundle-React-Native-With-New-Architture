package com.multiplebundle

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.*
import androidx.navigation.NavHostController
import com.facebook.react.ReactActivityDelegate
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint.fabricEnabled


@Composable
fun AppNavGraph(navController: NavHostController = rememberNavController()) {
    NavControllerHolder.navController = navController

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
        composable("screen3") {
            Screen3()
        }
    }
}

@Composable
fun Screen3() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("This is Native Screen 3")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            NavControllerHolder.navController?.popBackStack()
        }) {
            Text("Go Back")
        }
    }
}
