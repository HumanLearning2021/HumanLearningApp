package com.github.HumanLearning2021.HumanLearningApp.presenter

import android.net.Uri
import com.github.HumanLearning2021.HumanLearningApp.firestore.FirestoreDatasetInterface
import com.github.HumanLearning2021.HumanLearningApp.model.CategorizedPicture
import com.github.HumanLearning2021.HumanLearningApp.model.Category
import com.github.HumanLearning2021.HumanLearningApp.model.DatasetsManagement

object DatasetsManagementPresenter: DatasetsManagementPresenterInterface {

    val datasetsManager = DatasetsManagement

    override suspend fun putRepresentativePicture(picture: Uri, category: Category) {
        TODO("Not yet implemented")
    }

    override suspend fun putRepresentativePicture(picture: CategorizedPicture) {
        TODO("Not yet implemented")
    }

    override fun getDatasetNames(): Set<String> {
        TODO("Not yet implemented")
    }

    override fun editDatasetName(key: String, newName: String) {
        TODO("Not yet implemented")
    }

    fun getDataset(key: String): FirestoreDatasetInterface {
        return datasetsManager.getDataset(key)
    }

    fun initializeDataset(
        key: String,
        categories: Set<Category>
    ): FirestoreDatasetInterface {
        return datasetsManager.initializeDataset(key, categories)
    }

    fun deleteDataset(key: String) {
        return datasetsManager.deleteDataset(key)
    }
}