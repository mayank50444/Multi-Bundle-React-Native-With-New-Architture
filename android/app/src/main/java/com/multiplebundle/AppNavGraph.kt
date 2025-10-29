package com.multiplebundle

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun NativeScreen(onClickBiz1: () -> Unit, onClickBiz2: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("This is a native Jetpack Compose screen")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onClickBiz1) {
            Text("Launch Biz1 Bundle")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onClickBiz2) {
            Text("Launch Biz2 Bundle")
        }
    }
}
