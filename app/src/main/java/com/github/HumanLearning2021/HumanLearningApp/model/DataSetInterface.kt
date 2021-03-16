package com.github.HumanLearning2021.HumanLearningApp.model


/**
 * An interface representing the part of the model interacting with data sets
 */
interface DatasetInterface {
    suspend fun getPicture(category: Category): CategorizedPicture?
}