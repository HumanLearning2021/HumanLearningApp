package com.github.HumanLearning2021.HumanLearningApp.Presenter

import com.github.HumanLearning2021.HumanLearningApp.Model.*
import java.lang.IllegalArgumentException


/**
 * a class representing a dummy UI presenter
 */
class DummyUIPresenter:UIPresenter {
    private val dataSetInterface: DummyDataSetInterface = DummyDataSetInterface()


    override suspend fun getPicture(categoryString: String): CategorizedPicture =
            dataSetInterface.getPicture(getCategory(categoryString))

    private fun getCategory(name: String):Category {
        for(category in dataSetInterface.categories) {
            if(category.name.equals(name, ignoreCase = true)) return category
        }
        throw IllegalArgumentException("no category found that matches name")
    }

}