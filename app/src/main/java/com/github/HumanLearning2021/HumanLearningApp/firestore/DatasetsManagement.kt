package com.github.HumanLearning2021.HumanLearningApp.firestore

import com.github.HumanLearning2021.HumanLearningApp.model.CategorizedPicture
import com.github.HumanLearning2021.HumanLearningApp.model.Category

interface DatasetsManagement {

    /**
     * Retrieves the names of all the datasets available
     *
     * @return a set containing the names off all the available datasets
     */
    fun getDatasetNames(): Set<String> {
        TODO("not yet implemented")
    }

    /**
     * Retrieves the dataset
     *
     * @param name - the name of the desired dataset
     * @return an instance of FirestoreDatasetInterface
     * @throws IllegalArgumentException: if there exists no dataset of the specified name
     */
    fun getDataset(name: String): FirestoreDatasetInterface {
        TODO("not yet implemented")
    }

    /**
     * Initializes a new dataset
     *
     * @param name - the name of the desired dataset
     * @param categories - the set of categories the dataset contains, can be empty
     */
    fun initializeDataset(name: String, categories: Set<Category>) {
        TODO("not yet implemented")
    }

    /**
     * Changes the name of an existing dataset
     *
     * @param currentName - the current name of the dataset
     * @param newName - the new name the dataset should take
     * @throws IllegalArgumentException if there exists no dataset of the specified name
     */
    fun editDatasetName(currentName: String, newName: String) {
        TODO("not yet implemented")
    }

    /**
     * Deletes the dataset
     *
     * @param name - the name of the dataset to delete
     * @throws IllegalArgumentException if there exists no dataset of the specified name
     */
    fun deleteDataset(name: String) {
        TODO("not yet implemented")
    }
}