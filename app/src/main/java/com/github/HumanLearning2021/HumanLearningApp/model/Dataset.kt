package com.github.HumanLearning2021.HumanLearningApp.model

import android.os.Parcelable

/**
 * id should be used to uniquely identify the Dataset
 */
interface Dataset: Parcelable {
    val id: Any
    val name: String
    val categories: Set<Category>

    /**
     * Remove the category from the dataset
     *
     * @param category - the category to remove from the dataset
     * @return the dataset with the category removed
     * @throws IllegalArgumentException if the database does not contain the specified category
     */
    suspend fun removeCategory(category: Category): Dataset

    /**
     * Changes the name of an the dataset
     *
     * @param newName - the new name the dataset should take
     */
    suspend fun editDatasetName(newName: String): Dataset
}