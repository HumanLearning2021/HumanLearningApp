package com.github.HumanLearning2021.HumanLearningApp.model

import com.github.HumanLearning2021.HumanLearningApp.firestore.FirestoreDatasetInterface

object DatasetsManagement: DatasetsManagementInterface {
    override fun getDataset(key: String): FirestoreDatasetInterface {
        TODO("Not yet implemented")
    }

    override fun initializeDataset(
        key: String,
        categories: Set<Category>
    ): FirestoreDatasetInterface {
        TODO("Not yet implemented")
    }

    override fun deleteDataset(key: String) {
        TODO("Not yet implemented")
    }
}
