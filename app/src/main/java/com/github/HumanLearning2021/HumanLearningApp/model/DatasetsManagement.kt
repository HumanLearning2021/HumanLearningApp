package com.github.HumanLearning2021.HumanLearningApp.model

import com.github.HumanLearning2021.HumanLearningApp.firestore.FirestoreDatasetInterface

object DatasetsManagement: DatasetsManagementInterface {
    override fun getDataset(name: String): FirestoreDatasetInterface {
        TODO("Not yet implemented")
    }

    override fun initializeDataset(
        name: String,
        categories: Set<Category>
    ): FirestoreDatasetInterface {
        TODO("Not yet implemented")
    }

    override fun deleteDataset(name: String) {
        TODO("Not yet implemented")
    }
}
