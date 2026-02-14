package com.example.novel_summary.utils

import android.webkit.WebSettings
import android.webkit.WebView

object WebViewUtils {

    fun configureWebView(webView: WebView) {
        val settings = webView.settings

        // Essential settings - JavaScript MUST be enabled
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true

        // Performance optimizations
        settings.cacheMode = WebSettings.LOAD_DEFAULT
        settings.loadsImagesAutomatically = true

        // Security settings
        settings.setSupportMultipleWindows(false)
        settings.setAllowFileAccess(false)
        settings.setAllowContentAccess(false)
        settings.javaScriptCanOpenWindowsAutomatically = false
        settings.setSafeBrowsingEnabled(true)

        // User experience
        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true
        settings.builtInZoomControls = true
        settings.displayZoomControls = false

        // Modern web features
        settings.mediaPlaybackRequiresUserGesture = false
        settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

        // Enable hardware acceleration
        webView.setLayerType(WebView.LAYER_TYPE_HARDWARE, null)

        // IMPORTANT: Allow JavaScript interfaces
        settings.javaScriptEnabled = true
    }

    fun extractCleanText(webView: WebView): String {
        val jsScript = """
            (function() {
                // Remove scripts, styles, and unwanted elements
                var elementsToRemove = document.querySelectorAll(
                    'script, style, nav, header, footer, aside, iframe, .ads, .advertisement, .ad-banner, .popup, .modal'
                );
                elementsToRemove.forEach(function(el) {
                    el.remove();
                });
                
                // Try to find main content container (common selectors for novels)
                var content = document.querySelector('.entry-content') ||
                             document.querySelector('.chapter-content') ||
                             document.querySelector('.content') ||
                             document.querySelector('.novel-content') ||
                             document.querySelector('.text') ||
                             document.querySelector('.chapter-text') ||
                             document.querySelector('.post-content') ||
                             document.querySelector('article') ||
                             document.body;
                
                return content ? content.innerText.trim() : document.body.innerText.trim();
            })()
        """.trimIndent()

        return jsScript
    }

    fun extractPageTitle(webView: WebView): String {
        val jsScript = """
            (function() {
                return document.title || document.querySelector('h1')?.innerText || 'Untitled';
            })()
        """.trimIndent()
        return jsScript
    }
}