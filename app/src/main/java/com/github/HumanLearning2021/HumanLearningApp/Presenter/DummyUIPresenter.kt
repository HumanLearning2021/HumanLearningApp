package com.github.HumanLearning2021.HumanLearningApp.Presenter

import com.github.HumanLearning2021.HumanLearningApp.Model.*
import java.lang.IllegalArgumentException


/**
 * a class representing a dummy UI presenter
 */
class DummyUIPresenter:UIPresenter {
    private val dataSetInterface: DummyDataSetInterface = DummyDataSetInterface()


    /**
     * Allows to retrieve a picture fron the dummy dataset
     *
     * @param categoryString the name of the category of the picture to retrieve. Can be "knife", "fork", or "spoon"
     * @throws IllegalArgumentException if the string provided doesn't match any of "knife", "fork", or "spoon"
     */
    override suspend fun getPicture(categoryString: String): CategorizedPicture =
            dataSetInterface.getPicture(getCategory(categoryString))

    private fun getCategory(name: String):Category {
        for(category in dataSetInterface.categories) {
            if(category.name.equals(name, ignoreCase = true)) return category
        }
        throw IllegalArgumentException("no category found that matches name")
    }

}