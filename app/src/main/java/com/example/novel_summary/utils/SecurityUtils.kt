package com.example.novel_summary.utils

import android.content.Context
import android.content.pm.PackageManager
import android.webkit.WebView

object SecurityUtils {

    fun configureSecureWebView(webView: WebView) {
        val settings = webView.settings

        // Disable file access
        settings.setAllowFileAccess(false)
        settings.setAllowContentAccess(false)

        // Disable JavaScript from opening windows automatically
        settings.javaScriptCanOpenWindowsAutomatically = false

        // Enable safe browsing
        settings.setSafeBrowsingEnabled(true)
    }

    fun isPackageInstalled(context: Context, packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
}