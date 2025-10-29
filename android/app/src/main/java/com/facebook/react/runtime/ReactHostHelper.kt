package com.facebook.react.runtime

import com.facebook.react.bridge.JSBundleLoader
import com.facebook.react.interfaces.TaskInterface
import com.facebook.react.runtime.internal.bolts.Task
import kotlin.coroutines.Continuation
import kotlin.jvm.internal.Intrinsics

class ReactHostHelper(
    private val delegate: ReactHostImpl,
) {
    fun loadBundle(bundleLoader: JSBundleLoader): Boolean? {
        Intrinsics.checkNotNullParameter(bundleLoader, "bundlerLoader")
        val task = delegate.loadBundle(bundleLoader)


        task.waitForCompletion()

        return task.getResult()
    }
    fun getOrCreateReactInstance() {
        delegate.isInstanceInitialized
    }
}