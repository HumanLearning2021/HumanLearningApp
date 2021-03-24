package com.github.HumanLearning2021.HumanLearningApp.presenter

import com.github.HumanLearning2021.HumanLearningApp.model.*
import java.lang.IllegalArgumentException


/**
 * a class representing a dummy UI presenter
 */
class DummyUIPresenter(val databaseService: DummyDatabaseService) {

    /**
     * Allows to retrieve a picture fron the dummy dataset
     *
     * @param categoryName the name of the category of the picture to retrieve. Can be "knife", "fork", or "spoon"
     * @throws IllegalArgumentException if the string provided doesn't match any of "knife", "fork", or "spoon"
     */

    suspend fun getPicture(categoryName: String): CategorizedPicture? {
        return databaseService.getPicture(DummyCategory(categoryName, null))
    }

    suspend fun putPicture(picture: android.net.Uri, categoryName: String): CategorizedPicture {
        var category = databaseService.getCategory(categoryName)

        if(category == null)
            category = databaseService.putCategory(categoryName)

        return databaseService.putPicture(picture, category)
    }
}

