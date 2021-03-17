package com.github.HumanLearning2021.HumanLearningApp.presenter

import android.graphics.drawable.Drawable
import com.github.HumanLearning2021.HumanLearningApp.model.CategorizedPicture
import com.github.HumanLearning2021.HumanLearningApp.model.Category


/**
 * An interface representing the presenter (in the sense of the MVP design pattern) of the user interface
 */
interface UIPresenter {

    /**
     * A function to retrieve a picture from the data set given a category
     *
     * @param categoryName the name of the category of the image to be retrieved
     * @return a CategorizedPicture from the desired category. Null if no picture of the desired
     * category is present in the dataset.
     * @throws IllegalArgumentException if the provided category is not present in the dataset
     */
    suspend fun getPicture(categoryName: String): CategorizedPicture?

    /**
     * A function that allows to put a picture in the dataset
     *
     * @param picture the picture to put in the dataset
     * @param categoryName the name of the category to which the picture belongs
     * @return a Categorized picture built using 'picture' and 'category'
     * @throws IllegalArgumentException if the category provided is not present in the dataset
     */
    suspend fun putPicture(picture: Drawable, categoryName: String): CategorizedPicture
}