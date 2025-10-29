package com.multiplebundle

import android.app.Activity
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.facebook.react.ReactActivity
import com.facebook.react.ReactDelegate
import com.facebook.react.ReactRootView
import com.facebook.react.bridge.JSBundleLoader
import com.facebook.react.runtime.ReactHostHelper
import com.facebook.react.runtime.ReactHostImpl

@Composable
fun ReactBizScreen() {
    val context = LocalContext.current
    val reactRootView = remember { ReactRootView(context) }

    //    override fun createReactActivityDelegate(): ReactActivityDelegate =
//        MultipleReactActivityDelegate(this, mainComponentName, fabricEnabled)

    LaunchedEffect(Unit) {
        val delegate = MultipleReactActivityDelegate(
            context as ReactActivity,
            "BizBundle",
            true
        )

        delegate.onCreate(null)


        // Load app component
        delegate.loadApp("BizBundle")
    }

    AndroidView(
        factory = { reactRootView },
//        modifier = Modifier.fillMaxSize()
    )
}
