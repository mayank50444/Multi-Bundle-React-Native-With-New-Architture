package com.multiplebundle

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.*
import androidx.navigation.NavHostController
import com.facebook.react.ReactActivityDelegate
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint.fabricEnabled


@Composable
fun AppNavGraph(navController: NavHostController = rememberNavController()) {

    NavHost(navController = navController, startDestination = "split_screen") {
        composable("split_screen") {
            SplitScreen()
        }
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

@Composable
fun SplitScreen() {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Native screen - 50% (top half)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.5f)
                .border(
                    width = 2.dp,
                    color = Color.Gray,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(4.dp)
        ) {
            NativeScreen(
                onClickBiz1 = { /* No navigation needed, bundles are always visible */ },
                onClickBiz2 = { /* No navigation needed, bundles are always visible */ }
            )
        }
        
        // React Native bundles - 50% (bottom half split into two 25% sections)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.5f)
        ) {
            // Bundle 1 - 25% (left quarter)
            Box(
                modifier = Modifier
                    .weight(0.5f)
                    .fillMaxHeight()
                    .border(
                        width = 2.dp,
                        color = Color.Blue,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(4.dp)
            ) {
                ReactBizScreen(bundleName = "Biz1Bundle")
            }
            
            // Bundle 2 - 25% (right quarter)  
            Box(
                modifier = Modifier
                    .weight(0.5f)
                    .fillMaxHeight()
                    .border(
                        width = 2.dp,
                        color = Color.Green,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(4.dp)
            ) {
                ReactBizScreen(bundleName = "Biz2Bundle")
            }
        }
    }
}
