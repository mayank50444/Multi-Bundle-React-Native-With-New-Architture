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
import com.facebook.react.ReactInstanceEventListener
import com.facebook.react.ReactInstanceManager
import com.facebook.react.ReactRootView
import com.facebook.react.bridge.JSBundleLoader
import com.facebook.react.bridge.ReactContext
import com.facebook.react.common.annotations.DeprecatedInNewArchitecture
import com.facebook.react.defaults.DefaultReactActivityDelegate
import com.facebook.react.internal.featureflags.ReactNativeFeatureFlags.enableBridgelessArchitecture
import com.facebook.react.runtime.ReactHostHelper
import com.facebook.react.runtime.ReactHostImpl
import com.facebook.systrace.Systrace.traceSection

class MultipleReactActivityDelegate(
    activity: ReactActivity,
    mainComponentName: String,
    fabricEnabled: Boolean,
) : DefaultReactActivityDelegate(activity, mainComponentName, fabricEnabled) {
    private var mReactDelegate: ReactDelegate? = null

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

                if (enableBridgelessArchitecture()) {
                    this.mReactDelegate =
                        ReactDelegate(
                            this.plainActivity,
                            this.reactHost,
                            mainComponentName,
                            launchOptions,
                        )

                    reactHost?.start()?.waitForCompletion()
                    val result =
                        helper.loadBundle(
                            JSBundleLoader.createAssetLoader(this.reactActivity, "assets://biz1.android.bundle", false),
                        )

                    Log.i("TestApp", "load biz bundle ==> $result")
                    this.loadApp(mainComponentName)
                } else {
                    this.mReactDelegate =
                        object : ReactDelegate(
                            this.plainActivity,
                            this.reactNativeHost,
                            mainComponentName,
                            launchOptions,
                            this.isFabricEnabled,
                        ) {
                            override fun createRootView(): ReactRootView {
                                var rootView: ReactRootView? = this@MultipleReactActivityDelegate.createRootView()
                                if (rootView == null) {
                                    rootView = super.createRootView()
                                }

                                return rootView!!
                            }
                        }

                    reactNativeHost.reactInstanceManager.addReactInstanceEventListener(
                        object : ReactInstanceEventListener {
                            override fun onReactContextInitialized(context: ReactContext) {
                                Log.i("TestApp", "Multiple onReactContextInitialized")

                                val instance = reactNativeHost.reactInstanceManager.currentReactContext?.catalystInstance
                                instance?.loadScriptFromAssets(context.assets, "assets://biz.android.bundle", false)
                                Log.i("TestApp", "loaded biz bundle")
                                if (mainComponentName != null) {
                                    try {
                                        this@MultipleReactActivityDelegate.loadApp(mainComponentName)
                                    } catch (e: Exception) {
                                        Log.e("TestApp", "load app $mainComponentName")
                                    }
                                }
                            }
                        },
                    )
                    reactNativeHost.reactInstanceManager.createReactContextInBackground()
                }
            },
        )
    }

    private fun loadAppOldWay() {
    }

    override fun getReactDelegate(): ReactDelegate = mReactDelegate!!

    @DeprecatedInNewArchitecture(message = "Use getReactHost()")
    override fun getReactInstanceManager(): ReactInstanceManager = mReactDelegate!!.reactInstanceManager

    public override fun loadApp(appKey: String?) {
        mReactDelegate!!.loadApp(appKey)
        plainActivity.setContentView(mReactDelegate!!.reactRootView)
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
//        if (mPermissionsCallback != null) {
//            mPermissionsCallback!!.invoke()
//            mPermissionsCallback = null
//        }
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

    override fun onBackPressed(): Boolean = mReactDelegate!!.onBackPressed()

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
