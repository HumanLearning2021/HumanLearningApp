package com.github.HumanLearning2021.HumanLearningApp.presenter

import com.github.HumanLearning2021.HumanLearningApp.model.CategorizedPicture
import com.github.HumanLearning2021.HumanLearningApp.model.Category

interface DatasetsManagementPresenterInterface {
    /**
     * Adds a representative picture to the category. If there is already a representative picture assigned it will be overwritten.
     *
     * @param picture - the picture to put as a representative
     * @param category - the category whose representative picture we want to change
     * @throws IllegalArgumentException if the dataset does not contain the specified category
     */
    suspend fun putRepresentativePicture(picture: android.net.Uri, category: Category)

    /**
     * Adds a representative picture to the category. If there is already a representative picture assigned it will be overwritten.
     *
     * @param picture - the picture to put as a representative
     * @throws IllegalArgumentException if the dataset does not contain the specified category
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
     * @param key - the unique key of the dataset to edit
     * @param newName - the new name the dataset should take
     * @throws IllegalArgumentException if there exists no dataset of the specified name
     */
    fun editDatasetName(key: String, newName: String)
}