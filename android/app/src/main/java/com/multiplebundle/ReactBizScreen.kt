package com.multiplebundle

import android.app.Activity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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
fun ReactBizScreen(bundleName: String) {
    val context = LocalContext.current
    var reactRootView: ReactRootView? by remember { mutableStateOf(null) }

    val bundlePath = when(bundleName) {
        "Biz1Bundle" -> "assets://biz1.android.bundle"
        "Biz2Bundle" -> "assets://biz2.android.bundle"
        else -> "assets://biz.android.bundle"
    }

    LaunchedEffect(Unit) {
        val delegate = MultipleReactActivityDelegate(
            context as ReactActivity,
            bundleName,
            true,
            bundlePath
        )

        delegate.onCreate(null)


        
        // Get the ReactRootView from the delegate
        reactRootView = delegate.getReactRootView()
    }

    reactRootView?.let { rootView ->
        AndroidView(
            factory = { rootView },
            modifier = Modifier.fillMaxSize()
        )
    }
}
