package com.github.HumanLearning2021.HumanLearningApp.view

import android.app.AlertDialog
import android.content.Context
import android.view.MenuItem

object FragmentOptionsUtil {
    fun displayInfoMenu(
        item: MenuItem,
        infoItemId: Int,
        title: String,
        message: String,
        context: Context?
    ): Boolean {
        return when (item.itemId) {
            /**
             * When the info menu button is clicked, display information to the user about
             * the possible actions.
             */
            infoItemId -> {
                AlertDialog.Builder(context)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setTitle(title)
                    .setMessage(message)
                    .show()
                true
            }
            else -> {
                true
            }
        }
    }
}