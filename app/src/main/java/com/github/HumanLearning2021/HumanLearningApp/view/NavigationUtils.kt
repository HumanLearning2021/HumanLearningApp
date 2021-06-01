package com.github.HumanLearning2021.HumanLearningApp.view

import androidx.activity.OnBackPressedCallback
import androidx.navigation.NavController

class NavigationUtils {
    companion object {
        fun createOnBackPressedCallback(navController: NavController) =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    navController.popBackStack()
                }


            }

        fun destroyCallback(callback: OnBackPressedCallback) {
            callback.isEnabled = false
            callback.remove()
        }
    }
}