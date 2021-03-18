package com.github.HumanLearning2021.HumanLearningApp.presenter

import com.github.HumanLearning2021.HumanLearningApp.model.*
import java.lang.IllegalArgumentException


/**
 * a class representing a dummy UI presenter
 */
class DummyUIPresenter: UIPresenter {
    private val dataSetInterface: DummyDatasetInterface = DummyDatasetInterface()


    /**
     * Allows to retrieve a picture fron the dummy dataset
     *
     * @param categoryString the name of the category of the picture to retrieve. Can be "knife", "fork", or "spoon"
     * @throws IllegalArgumentException if the string provided doesn't match any of "knife", "fork", or "spoon"
     */
    override suspend fun getPicture(categoryString: String): CategorizedPicture? {
        return dataSetInterface.getPicture(DummyCategory(categoryString))
    }
}