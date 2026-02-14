package com.example.novel_summary.utils

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AlertDialog

object DialogUtils {

    fun showLoadingDialog(context: Context, message: String = "Loading..."): AlertDialog {
        return AlertDialog.Builder(context)
            .setMessage(message)
            .setCancelable(false)
            .create().apply {
                show()
            }
    }

    fun showConfirmationDialog(
        context: Context,
        title: String,
        message: String,
        positiveText: String = "Yes",
        negativeText: String = "No",
        positiveAction: () -> Unit,
        negativeAction: (() -> Unit)? = null
    ) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveText) { _, _ -> positiveAction() }
            .setNegativeButton(negativeText) { _, _ -> negativeAction?.invoke() }
            .show()
    }

    fun showErrorDialog(
        context: Context,
        title: String = "Error",
        message: String,
        buttonText: String = "OK"
    ) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(buttonText, null)
            .show()
    }
}