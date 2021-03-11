package com.github.HumanLearning2021.HumanLearningApp.Presenter

import com.github.HumanLearning2021.HumanLearningApp.Model.CategorizedPicture


/**
 * an interface representing the presenter of the user interface
 */
interface UIPresenter {
<<<<<<< HEAD
    fun getPicture(category: String): CategorizedPicture
=======
    suspend fun getPicture(category: String): CategorizedPicture
>>>>>>> 01f8cbdd24325478107733d762bf14268ed46a70
}