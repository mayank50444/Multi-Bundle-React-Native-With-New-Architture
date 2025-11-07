package com.multiplebundle

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import com.facebook.react.ReactActivity
import com.facebook.react.ReactDelegate
import com.facebook.react.ReactRootView
import com.facebook.react.bridge.JSBundleLoader
import com.facebook.react.bridge.ReactContext
import com.facebook.react.defaults.DefaultReactActivityDelegate
import com.facebook.react.runtime.ReactHostHelper
import com.facebook.react.runtime.ReactHostImpl
import com.facebook.systrace.Systrace.traceSection

class MultipleReactActivityDelegate(
    activity: ReactActivity,
    mainComponentName: String,
    fabricEnabled: Boolean,
    private val bundlePath: String = "assets://biz.android.bundle"
) : DefaultReactActivityDelegate(activity, mainComponentName, fabricEnabled) {
    private var mReactDelegate: ReactDelegate? = null
    var onBackPressedCallback: (() -> Boolean)? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        val helper = ReactHostHelper(reactHost as ReactHostImpl)

        traceSection(
            0L,
            "ReactActivityDelegate.onCreate::init",
            Runnable {
                val mainComponentName = this.mainComponentName
                val launchOptions = this.composeLaunchOptions()
                val activity = reactActivity
                
                if (Build.VERSION.SDK_INT >= 26 && this.isWideColorGamutEnabled) {
                    activity.window.colorMode = ActivityInfo.COLOR_MODE_WIDE_COLOR_GAMUT
                }

                // New Architecture (Bridgeless) only
                this.mReactDelegate = ReactDelegate(
                    this.plainActivity,
                    this.reactHost,
                    mainComponentName,
                    launchOptions,
                )

                reactHost?.start()?.waitForCompletion()
                
                // Extract asset name from bundlePath (e.g., "biz1.android.bundle" from "assets://biz1.android.bundle")
                val assetName = bundlePath.removePrefix("assets://")
                
                // Check for local bundle using custom code push (only for business bundles)
                // Common bundle always uses assets, business bundles can use code push
                val localBundlePath = CustomCodePushManager.getBundleFile(mainComponentName, assetName)
                
                // Use local bundle if available, otherwise use asset bundle
                val bundleLoader = if (localBundlePath != null) {
                    Log.i("TestApp", "Using code push bundle for $mainComponentName: $localBundlePath")
                    JSBundleLoader.createFileLoader(localBundlePath)
                } else {
                    Log.i("TestApp", "Using asset bundle for $mainComponentName: $bundlePath")
                    JSBundleLoader.createAssetLoader(this.reactActivity, bundlePath, false)
                }
                
                val result = helper.loadBundle(bundleLoader)
                Log.i("TestApp", "load bundle $bundlePath ==> $result")
                this.loadApp(mainComponentName)
            },
        )
    }

    override fun getReactDelegate(): ReactDelegate = mReactDelegate!!

    public override fun loadApp(appKey: String?) {
        mReactDelegate!!.loadApp(appKey)
    }

    fun getReactRootView(): ReactRootView? {
        return mReactDelegate?.reactRootView as? ReactRootView
    }

    override fun onUserLeaveHint() {
        if (mReactDelegate != null) {
            mReactDelegate!!.onUserLeaveHint()
        }
    }

    override fun onPause() {
        mReactDelegate!!.onHostPause()
    }

    override fun onResume() {
        mReactDelegate!!.onHostResume()
    }

    override fun onDestroy() {
        mReactDelegate!!.onHostDestroy()
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
    ) {
        mReactDelegate!!.onActivityResult(requestCode, resultCode, data, true)
    }

    override fun onKeyDown(
        keyCode: Int,
        event: KeyEvent?,
    ): Boolean = mReactDelegate!!.onKeyDown(keyCode, event)

    override fun onKeyUp(
        keyCode: Int,
        event: KeyEvent?,
    ): Boolean = mReactDelegate!!.shouldShowDevMenuOrReload(keyCode, event)

    override fun onKeyLongPress(
        keyCode: Int,
        event: KeyEvent?,
    ): Boolean = mReactDelegate!!.onKeyLongPress(keyCode)

    override fun onBackPressed(): Boolean {
        // Check if custom callback is set and returns true (handled)
        if (onBackPressedCallback?.invoke() == true) {
            return true
        }
        // Otherwise, let React Native handle it
        return mReactDelegate!!.onBackPressed()
    }

    override fun onNewIntent(intent: Intent?): Boolean = mReactDelegate!!.onNewIntent(intent)

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        mReactDelegate!!.onWindowFocusChanged(hasFocus)
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        mReactDelegate!!.onConfigurationChanged(newConfig)
    }

    /**
     * Get the current [ReactContext] from ReactHost or ReactInstanceManager
     *
     *
     * Do not store a reference to this, if the React instance is reloaded or destroyed, this
     * context will no longer be valid.
     */
    override fun getCurrentReactContext(): ReactContext = mReactDelegate!!.currentReactContext!!
}
