package com.github.HumanLearning2021.HumanLearningApp.presenter

import android.graphics.drawable.Drawable
import com.github.HumanLearning2021.HumanLearningApp.model.*
import java.io.Serializable
import java.lang.IllegalArgumentException


/**
 * a class representing a dummy UI presenter
 */
class DummyUIPresenter {
    val dataSetInterface: DummyDatasetInterface = DummyDatasetInterface()


    /**
     * Allows to retrieve a picture fron the dummy dataset
     *
     * @param categoryName the name of the category of the picture to retrieve. Can be "knife", "fork", or "spoon"
     * @throws IllegalArgumentException if the string provided doesn't match any of "knife", "fork", or "spoon"
     */
    suspend fun getPicture(categoryName: String): CategorizedPicture? {
        return dataSetInterface.getPicture(DummyCategory(categoryName))
    }

    suspend fun putPicture(picture: Serializable, categoryName: String): CategorizedPicture {
        var category = dataSetInterface.getCategory(categoryName)

        if(category == null)
            category = dataSetInterface.putCategory(categoryName)

        return dataSetInterface.putPicture(picture, category)
    }
}

