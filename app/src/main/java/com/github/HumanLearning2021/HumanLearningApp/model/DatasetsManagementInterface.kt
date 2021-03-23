package com.github.HumanLearning2021.HumanLearningApp.model

import com.github.HumanLearning2021.HumanLearningApp.firestore.FirestoreDatasetInterface

interface DatasetsManagementInterface {

    /**
     * Retrieves the dataset
     *
     * @param key - the unique key of the desired dataset
     * @return an instance of FirestoreDatasetInterface
     * @throws IllegalArgumentException: if there exists no dataset of the specified name
     */
    fun getDataset(key: String): FirestoreDatasetInterface

    /**
     * Initializes a new dataset
     *
     * @param name - the name of the dataset
     * @param categories - the set of categories the dataset contains, can be empty
     */
    fun initializeDataset(name: String, categories: Set<Category>): FirestoreDatasetInterface

    /**
     * Deletes the dataset
     *
     * @param key - the unique key of the desired dataset
     * @throws IllegalArgumentException if there exists no dataset of the specified name
     */
    fun deleteDataset(key: String)
}