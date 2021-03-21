package com.github.HumanLearning2021.HumanLearningApp.presenter

import android.graphics.drawable.Drawable
import com.github.HumanLearning2021.HumanLearningApp.model.*
import java.io.Serializable
import java.lang.IllegalArgumentException


/**
 * a class representing a dummy UI presenter
 */
class DummyUIPresenter(val datasetInterface: DatasetInterface) {
    /**
     * Allows to retrieve a picture fron the dummy dataset
     *
     * @param categoryName the name of the category of the picture to retrieve. Can be "knife", "fork", or "spoon"
     * @throws IllegalArgumentException if the string provided doesn't match any of "knife", "fork", or "spoon"
     */

    suspend fun getPicture(categoryName: String): CategorizedPicture? {
        return datasetInterface.getPicture(DummyCategory(categoryName))
    }

    suspend fun putPicture(picture: android.net.Uri, categoryName: String): CategorizedPicture {
        var category = datasetInterface.getCategory(categoryName)

        if(category == null)
            category = datasetInterface.putCategory(categoryName)

        return datasetInterface.putPicture(picture, category)
    }
}

