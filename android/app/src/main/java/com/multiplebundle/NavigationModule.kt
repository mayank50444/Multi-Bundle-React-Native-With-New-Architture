package com.multiplebundle

import android.os.Handler
import android.os.Looper
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.module.annotations.ReactModule

@ReactModule(name = NavigationModule.NAME)
class NavigationModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

    override fun getName(): String = NAME

    @ReactMethod
    fun openScreen3() {
        val mainHandler = Handler(Looper.getMainLooper())
        mainHandler.post {
            NavControllerHolder.navController?.navigate("screen3")
        }
    }

    companion object {
        const val NAME = "NavigationModule"
    }
}


