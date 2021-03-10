package com.github.HumanLearning2021.HumanLearningApp.Presenter

import com.github.HumanLearning2021.HumanLearningApp.Model.CategorizedPicture

interface UIPresenter {
    fun getPicture(category: String): CategorizedPicture
}