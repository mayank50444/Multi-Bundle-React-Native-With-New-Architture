package com.multiplebundle

import android.content.Context
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.TimeUnit

object CustomCodePushManager {
    private const val TAG = "CustomCodePush"
    private var appContext: Context? = null
    private var baseUrl: String? = null
    private var accessToken: String? = null
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()
    
    fun initialize(context: Context, serverBaseUrl: String? = null, token: String? = null) {
        appContext = context.applicationContext
        baseUrl = serverBaseUrl
        accessToken = token
        Log.d(TAG, "CustomCodePushManager initialized${if (serverBaseUrl != null) " with server: $serverBaseUrl${if (token != null) " (with token)" else ""}" else " (download disabled)"}")
    }
    
    private fun isBusinessBundle(bundleName: String, assetName: String): Boolean {
        return bundleName.startsWith("Biz") || 
               assetName.contains("biz1") || 
               assetName.contains("biz2") ||
               assetName.contains("biz.")
    }
    
    fun getBundleFile(bundleName: String, assetName: String): String? {
        if (!isBusinessBundle(bundleName, assetName)) {
            return null
        }
        
        val context = appContext ?: return null
        
        val bundlesDir = File(context.filesDir, "bundles")
        if (!bundlesDir.exists()) {
            bundlesDir.mkdirs()
        }
        
        val bundleFile = File(bundlesDir, assetName)
        
        return if (bundleFile.exists() && bundleFile.length() > 0) {
            Log.d(TAG, "Found local bundle for $bundleName: ${bundleFile.absolutePath}")
            bundleFile.absolutePath
        } else {
            Log.d(TAG, "No local bundle for $bundleName, using asset bundle")
            null
        }
    }
    
    fun downloadBundle(
        bundleName: String,
        assetName: String,
        callback: (success: Boolean, filePath: String?, error: String?) -> Unit
    ) {
        if (!isBusinessBundle(bundleName, assetName)) {
            callback(false, null, "Not a business bundle")
            return
        }
        
        val context = appContext ?: run {
            callback(false, null, "Context not initialized")
            return
        }
        
        val url = baseUrl ?: run {
            callback(false, null, "Server URL not configured")
            return
        }
        
        val downloadUrl = if (url.contains("firebasestorage")) {
            val tokenParam = accessToken?.let { "&token=$it" } ?: ""
            "$url/Bundle%2F$assetName?alt=media$tokenParam"
        } else {
            "$url/$assetName"
        }
        
        Log.d(TAG, "Downloading bundle for $bundleName from: $downloadUrl")
        Thread {
            try {
                val request = Request.Builder()
                    .url(downloadUrl)
                    .build()
                
                val response = httpClient.newCall(request).execute()
                
                if (!response.isSuccessful) {
                    callback(false, null, "HTTP ${response.code}: ${response.message}")
                    return@Thread
                }
                
                val responseBody = response.body ?: run {
                    callback(false, null, "Empty response body")
                    return@Thread
                }
                
                val bundlesDir = File(context.filesDir, "bundles")
                if (!bundlesDir.exists()) {
                    bundlesDir.mkdirs()
                }
                
                val bundleFile = File(bundlesDir, assetName)
                FileOutputStream(bundleFile).use { output ->
                    responseBody.byteStream().use { input ->
                        input.copyTo(output)
                    }
                }
                
                if (bundleFile.exists() && bundleFile.length() > 0) {
                    Log.d(TAG, "Successfully downloaded bundle for $bundleName: ${bundleFile.absolutePath}")
                    callback(true, bundleFile.absolutePath, null)
                } else {
                    callback(false, null, "Downloaded file is empty or missing")
                }
                
            } catch (e: IOException) {
                Log.e(TAG, "Error downloading bundle for $bundleName", e)
                callback(false, null, "Network error: ${e.message}")
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error downloading bundle for $bundleName", e)
                callback(false, null, "Error: ${e.message}")
            }
        }.start()
    }
    
    fun checkForUpdates(bundleName: String, assetName: String) {
        if (!isBusinessBundle(bundleName, assetName)) {
            return
        }
        
        val context = appContext ?: return
        val bundleFile = File(File(context.filesDir, "bundles"), assetName)
        
        downloadBundle(bundleName, assetName) { success, filePath, error ->
            if (success) {
                Log.d(TAG, "Update downloaded successfully for $bundleName: $filePath")
            } else {
                Log.w(TAG, "Failed to download update for $bundleName: $error")
            }
        }
    }
}

