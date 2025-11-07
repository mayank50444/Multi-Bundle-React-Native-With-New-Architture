package com.multiplebundle

import android.content.Context
import android.util.Log
import java.io.File

/**
 * Simple custom code push manager - Step 1: Basic file checking
 * 
 * This manager checks if a local bundle file exists and returns its path.
 * If no local bundle exists, returns null to use the asset bundle.
 * 
 * NOTE: Only handles business bundles (biz1, biz2), NOT the common bundle.
 */
object CustomCodePushManager {
    private const val TAG = "CustomCodePush"
    private var appContext: Context? = null
    
    /**
     * Initialize the manager with app context
     */
    fun initialize(context: Context) {
        appContext = context.applicationContext
        Log.d(TAG, "CustomCodePushManager initialized")
    }
    
    /**
     * Check if this is a business bundle (not common bundle)
     */
    private fun isBusinessBundle(bundleName: String, assetName: String): Boolean {
        // Business bundles: Biz1Bundle, Biz2Bundle, etc. or assets like biz1.android.bundle, biz2.android.bundle
        return bundleName.startsWith("Biz") || 
               assetName.contains("biz1") || 
               assetName.contains("biz2") ||
               assetName.contains("biz.")
    }
    
    /**
     * Get the local bundle file path if it exists, otherwise return null
     * 
     * @param bundleName The bundle name (e.g., "Biz1Bundle", "Biz2Bundle", "CommonBundle")
     * @param assetName The asset name (e.g., "biz1.android.bundle", "common.android.bundle")
     * @return File path to local bundle if exists, null to use asset bundle
     */
    fun getBundleFile(bundleName: String, assetName: String): String? {
        // Only apply code push to business bundles, not common bundle
        if (!isBusinessBundle(bundleName, assetName)) {
            Log.d(TAG, "Skipping code push for common bundle: $bundleName")
            return null
        }
        
        val context = appContext ?: return null
        
        // Create directory for storing bundles
        val bundlesDir = File(context.filesDir, "bundles")
        if (!bundlesDir.exists()) {
            bundlesDir.mkdirs()
        }
        
        // Check if local bundle file exists
        val bundleFile = File(bundlesDir, assetName)
        
        return if (bundleFile.exists() && bundleFile.length() > 0) {
            Log.d(TAG, "Found local bundle for $bundleName: ${bundleFile.absolutePath}")
            bundleFile.absolutePath
        } else {
            Log.d(TAG, "No local bundle for $bundleName, using asset bundle")
            null
        }
    }
}

