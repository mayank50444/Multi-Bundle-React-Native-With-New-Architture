package com.multiplebundle

import android.app.Application
import android.util.Log
import com.facebook.common.logging.FLog
import com.facebook.react.PackageList
import com.facebook.react.ReactApplication
import com.facebook.react.ReactHost

import com.facebook.react.ReactNativeHost
import com.facebook.react.ReactPackage
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint.load
import com.facebook.react.defaults.DefaultReactHost.getDefaultReactHost
import com.facebook.react.defaults.DefaultReactNativeHost
import com.facebook.react.internal.featureflags.ReactNativeFeatureFlags
import com.facebook.react.soloader.OpenSourceMergedSoMapping
import com.facebook.soloader.SoLoader

class MainApplication :
    Application(),
    ReactApplication {
    override val reactNativeHost: ReactNativeHost =
        object : DefaultReactNativeHost(this) {
            override fun getPackages(): List<ReactPackage> =
                PackageList(this).packages.apply {
                    // Packages that cannot be autolinked yet can be added manually here, for example:
                    // add(MyReactNativePackage())
                }

            override fun getJSMainModuleName(): String = "index.common"

            override fun getBundleAssetName(): String = "common.android.bundle"

            override fun getUseDeveloperSupport(): Boolean = BuildConfig.DEBUG

            override val isNewArchEnabled: Boolean = BuildConfig.IS_NEW_ARCHITECTURE_ENABLED
            override val isHermesEnabled: Boolean = BuildConfig.IS_HERMES_ENABLED
        }

    override val reactHost: ReactHost
        get() = getDefaultReactHost(applicationContext, reactNativeHost)


    override fun onCreate() {
        super.onCreate()
        SoLoader.init(this, OpenSourceMergedSoMapping)
        FLog.setMinimumLoggingLevel(FLog.VERBOSE)
        load(bridgelessEnabled = true)
        
        val firebaseUrl = ""
        val accessToken = ""
        
        if (firebaseUrl.isNotEmpty()) {
            CustomCodePushManager.initialize(this, firebaseUrl, accessToken)
            CustomCodePushManager.checkForUpdates("Biz1Bundle", "biz1.android.bundle")
            CustomCodePushManager.checkForUpdates("Biz2Bundle", "biz2.android.bundle")
        } else {
            CustomCodePushManager.initialize(this)
        }
    }
}
