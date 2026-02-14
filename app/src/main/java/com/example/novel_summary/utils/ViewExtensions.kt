package com.example.novel_summary.utils

import android.view.View
import androidx.core.view.isGone
import androidx.core.view.isVisible

fun View.show() {
    isVisible = true
}

fun View.hide() {
    isVisible = false
}

fun View.gone() {
    isGone = true
}

fun View.visible() {
    isVisible = true
    isGone = false
}

fun View.toggleVisibility() {
    isVisible = !isVisible
}

fun View.setEnabledWithAlpha(enabled: Boolean) {
    this.isEnabled = enabled
    this.alpha = if (enabled) 1.0f else 0.5f
}