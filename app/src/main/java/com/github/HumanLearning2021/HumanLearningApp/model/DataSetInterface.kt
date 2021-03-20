package com.github.HumanLearning2021.HumanLearningApp.model

import android.graphics.drawable.Drawable
import java.io.Serializable


/**
 * An interface representing the part of the model interacting with data sets
 */
interface DatasetInterface {
    /**
     * A function to retrieve a picture from the data set given a category
     *
     * @param category the category of the image to be retrieved
     * @return a CategorizedPicture from the desired category. Null if no picture of the desired
     * category is present in the dataset.
     * @throws IllegalArgumentException if the provided category is not present in the dataset
     */
    suspend fun getPicture(category: Category): CategorizedPicture?


    /**
     * A function that allows to put a picture in the dataset
     *
     * @param picture the picture to put in the dataset. Must implement the serializable interface.
     * A way to do this is by creating a class containing an ID and an array of bytes containing the
     * PNG data.
     * @param category the category to which the picture belongs
     * @return a Categorized picture built using 'picture' and 'category'
     * @throws IllegalArgumentException if the category provided is not present in the dataset
     */
    suspend fun putPicture(picture: android.net.Uri, category: Category): CategorizedPicture

    /**
     * A function to retrieve a category
     *
     * @param categoryName the name of the desired category
     * @return the desired category if present, null otherwise
     */
    suspend fun getCategory(categoryName: String):Category?

    /**
     * A function to add a category to the dataset. If the provided name matches that of category
     * that is already present, nothing is done.
     *
     * @param categoryName the name of the category to add
     * @return the Category that was inserted, or was already present
     */
    suspend fun putCategory(categoryName: String):Category

    /**
     * A function to retrieve the set of categories present in the dataset
     *
     * @return the set of categories present in the dataset
     */
    suspend fun getCategories(): Set<Category>

}