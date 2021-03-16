package com.github.HumanLearning2021.HumanLearningApp.presenter

import com.github.HumanLearning2021.HumanLearningApp.model.CategorizedPicture


/**
 * an interface representing the presenter (in the sense of the MVP design pattern) of the user interface
 */
interface UIPresenter {
    suspend fun getPicture(category: String): CategorizedPicture?
}