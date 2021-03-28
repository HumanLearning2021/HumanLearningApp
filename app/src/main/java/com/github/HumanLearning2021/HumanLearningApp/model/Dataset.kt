package com.github.HumanLearning2021.HumanLearningApp.model

import java.io.Serializable

interface Dataset: Serializable {
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
}