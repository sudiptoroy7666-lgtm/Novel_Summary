package com.example.novel_summary.utils

/**
 * Singleton to hold large content between activities.
 * This avoids "Transaction too large" error when passing
 * large text content (like novel chapters) via Intent extras.
 *
 * Usage:
 * 1. Before starting ActivitySummary:
 *    ContentHolder.setContent(content, url, title)
 *
 * 2. In ActivitySummary.onCreate():
 *    val data = ContentHolder.getContent()
 *    ContentHolder.clear() // Free memory after reading
 */
object ContentHolder {
    private var _content: String = ""
    private var _url: String = ""
    private var _title: String = ""
    private var _timestamp: Long = 0

    /**
     * Store content to be passed to another activity
     */
    fun setContent(content: String, url: String, title: String) {
        _content = content
        _url = url
        _title = title
        _timestamp = System.currentTimeMillis()
    }

    /**
     * Retrieve stored content
     */
    fun getContent(): ContentData {
        return ContentData(
            content = _content,
            url = _url,
            title = _title,
            timestamp = _timestamp
        )
    }

    /**
     * Check if content is available
     */
    fun hasContent(): Boolean {
        return _content.isNotEmpty() && _url.isNotEmpty()
    }

    /**
     * Clear stored content to free memory
     * Always call this after reading content in the target activity
     */
    fun clear() {
        _content = ""
        _url = ""
        _title = ""
        _timestamp = 0
    }

    /**
     * Data class to hold all content information
     */
    data class ContentData(
        val content: String,
        val url: String,
        val title: String,
        val timestamp: Long
    ) {
        val isValid: Boolean
            get() = content.isNotEmpty() && url.isNotEmpty()
    }
}