package com.github.HumanLearning2021.HumanLearningApp.view

import android.app.AlertDialog
import android.content.Context
import android.view.MenuItem

/**
 * Util object for fragments.
 */
object FragmentOptionsUtil {
    /**
     * Helper function used to display an info menu alert dialog
     *
     * @param item the menu item that was clicked
     * @param infoItemId the item id of the info menu item
     * @param title the title of the alert dialog
     * @param message the message for the alert dialog to display
     * @param context the current context
     *
     * @return a boolean, necessary if this function is called in onOptionsItemSelected()
     */
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