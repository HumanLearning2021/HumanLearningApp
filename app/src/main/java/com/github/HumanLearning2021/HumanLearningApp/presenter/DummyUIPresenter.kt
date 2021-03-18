package com.github.HumanLearning2021.HumanLearningApp.presenter

import android.graphics.drawable.Drawable
import com.github.HumanLearning2021.HumanLearningApp.model.*
import java.lang.IllegalArgumentException


/**
 * a class representing a dummy UI presenter
 */
class DummyUIPresenter: UIPresenter {
    val dataSetInterface: DummyDatasetInterface = DummyDatasetInterface()


    /**
     * Allows to retrieve a picture fron the dummy dataset
     *
     * @param categoryString the name of the category of the picture to retrieve. Can be "knife", "fork", or "spoon"
     * @throws IllegalArgumentException if the string provided doesn't match any of "knife", "fork", or "spoon"
     */
    override suspend fun getPicture(categoryName: String): CategorizedPicture? {
        return dataSetInterface.getPicture(DummyCategory(categoryString))
    }

    override suspend fun putPicture(picture: Drawable, categoryString: String): CategorizedPicture {
        var category = dataSetInterface.getCategory(categoryString)

        if(category == null)
            category = dataSetInterface.putCategory(categoryString)

        return dataSetInterface.putPicture(picture, category)
    }
}
