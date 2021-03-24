package com.github.HumanLearning2021.HumanLearningApp.model

import android.graphics.drawable.Drawable
import com.github.HumanLearning2021.HumanLearningApp.firestore.FirestoreCategorizedPicture
import java.io.Serializable


/**
 * An interface representing the part of the model interacting with data sets
 */
interface DatabaseService {
    /**
     * A function to retrieve a picture from the data set given a category
     *
     * @param category the category of the image to be retrieved
     * @return a CategorizedPicture from the desired category. Null if no picture of the desired
     * category is present in the database.
     * @throws IllegalArgumentException if the provided category is not present in the database
     */
    suspend fun getPicture(category: Category): CategorizedPicture?

    /**
     * A function that allows to put a picture in the database
     *
     * @param picture the picture to put in the database
     * @param category the category to which the picture belongs
     * @return a Categorized picture built using 'picture' and 'category'
     * @throws IllegalArgumentException if the category provided is not present in the database
     */
    suspend fun putPicture(picture: android.net.Uri, category: Category): CategorizedPicture

    /**
     * A function to retrieve a category
     *
     * @param categoryName the name of the desired category
     * @return the desired category if present, null otherwise
     */
    suspend fun getCategory(categoryName: String): Category?

    /**
     * A function to add a category to the database. If the provided name matches that of category
     * that is already present, nothing is done.
     *
     * @param categoryName the name of the category to add
     * @return the Category that was inserted, or was already present
     */
    suspend fun putCategory(categoryName: String): Category

    /**
     * A function to retrieve the set of categories present in the database
     *
     * @return the set of categories present in the database
     */
    suspend fun getCategories(): Set<Category>

    /**
     * Retrieves the representative picture of the specified category
     *
     * @param category - the category whose representative picture we want to retrieve
     * @return the representative picture associated to the specified category
     * @throws IllegalArgumentException if the database does not contain the specified category
     */
    suspend fun getRepresentativePicture(category: Category): CategorizedPicture?

    /**
     * Retrieves all the pictures categorized with the specified category
     *
     * @param category - the category whose pictures we want to retrieve
     * @return the pictures categorized with the specified category
     * @throws IllegalArgumentException if the database does not contain the specified category
     */
    suspend fun getAllPictures(category: Category): Set<CategorizedPicture>

    /**
     * Remove the category from the database
     *
     * @param category - the category to remove from the database
     * @throws IllegalArgumentException if the database does not contain the specified category
     */
    suspend fun removeCategory(category: Category)

    /**
     * Removes the corresponding picture from the database
     *
     * @param picture - the picture to remove from the database
     * @throws IllegalArgumentException if the database does not contain the specified picture
     */
    suspend fun removePicture(picture: CategorizedPicture)

    /**
     * Creates a dataset and puts it into the database
     *
     * @param name - the name of the dataset
     * @param categories - the categories of the dataset
     * @return the dataset which was created
     */
    suspend fun putDataset(name: String, categories: Set<Category>): Dataset

    /**
     * Gets a dataset from the database
     *
     * @param name - the name of the desired dataset
     * @return the dataset
     */
    suspend fun getDataset(name: String): Dataset?

    /**
     * Deletes the specified dataset from the database
     *
     * @param name - the name of the dataset to delete
     * @throws IllegalArgumentException if there is no dataset of the specified name in the database
     */
    suspend fun deleteDataset(name: String)

    /**
     * Adds a representative picture to the category. If there is already a representative picture assigned it will be overwritten.
     *
     * @param picture - the picture to put as a representative
     * @param category - the category whose representative picture we want to change
     * @throws IllegalArgumentException if the database does not contain the specified category
     */
    suspend fun putRepresentativePicture(picture: android.net.Uri, category: Category)

    /**
     * Adds a representative picture to the category. If there is already a representative picture assigned it will be overwritten.
     *
     * @param picture - the picture to put as a representative
     * @throws IllegalArgumentException if the database does not contain the specified category
     */
    suspend fun putRepresentativePicture(picture: CategorizedPicture)

    /**
     * Retrieves the names of all the datasets available
     *
     * @return a set containing the names off all the available datasets
     */
    fun getDatasetNames(): Set<String>

    /**
     * Changes the name of an existing dataset
     *
     * @param oldName - the name of the dataset to edit
     * @param newName - the new name the dataset should take
     * @throws IllegalArgumentException if there exists no dataset of the specified name
     */
    fun editDatasetName(oldName: String, newName: String)
}