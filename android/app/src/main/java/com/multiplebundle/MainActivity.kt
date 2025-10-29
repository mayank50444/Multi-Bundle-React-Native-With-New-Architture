package com.multiplebundle

import android.os.Bundle
import android.util.Log
import com.facebook.react.ReactActivity
import com.facebook.react.ReactActivityDelegate
import com.facebook.react.ReactInstanceEventListener
import com.facebook.react.bridge.JSBundleLoader
import com.facebook.react.bridge.ReactContext
import com.facebook.react.bridge.ReactMarker
import com.facebook.react.bridge.ReactMarkerConstants
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint.fabricEnabled
import com.facebook.react.defaults.DefaultReactActivityDelegate
import com.facebook.react.internal.featureflags.ReactNativeFeatureFlags.enableBridgelessArchitecture
import com.facebook.react.runtime.ReactHostHelper
import com.facebook.react.runtime.ReactHostImpl
import androidx.activity.compose.setContent
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
class MainActivity : ReactActivity() {
    /**
     * Returns the name of the main component registered from JavaScript. This is used to schedule
     * rendering of the component.
     */
//    override fun getMainComponentName(): String = "BizBundle"
//    override fun getMainComponentName(): String = "BizBundle"

    /**
     * Returns the instance of the [ReactActivityDelegate]. We use [DefaultReactActivityDelegate]
     * which allows you to enable New Architecture with a single boolean flags [fabricEnabled]
     */

//    override fun createReactActivityDelegate(): ReactActivityDelegate =
//        MultipleReactActivityDelegate(this, mainComponentName, fabricEnabled)

    override fun onCreate(savedInstanceState: Bundle?) {

        Log.i("TestApp", "Activity onCreate ${(reactHost.hashCode())}")
        super.onCreate(savedInstanceState)
        setContent {
            AppNavGraph()
        }
    }

}
