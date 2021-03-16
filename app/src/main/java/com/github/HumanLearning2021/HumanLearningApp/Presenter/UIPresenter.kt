package com.github.HumanLearning2021.HumanLearningApp.Presenter

import com.github.HumanLearning2021.HumanLearningApp.Model.CategorizedPicture


/**
 * an interface representing the presenter (in the sense of the MVP design pattern) of the user interface
 */
interface UIPresenter {
    suspend fun getPicture(category: String): CategorizedPicture?
}