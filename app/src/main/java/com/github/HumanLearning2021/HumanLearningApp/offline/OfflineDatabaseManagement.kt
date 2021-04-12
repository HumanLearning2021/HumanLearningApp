package com.github.HumanLearning2021.HumanLearningApp.offline

import android.net.Uri
import com.github.HumanLearning2021.HumanLearningApp.model.CategorizedPicture
import com.github.HumanLearning2021.HumanLearningApp.model.Category
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.Dataset

class OfflineDatabaseManagement: DatabaseManagement {
    override suspend fun getPicture(category: Category): CategorizedPicture? {
        TODO("Not yet implemented")
    }

    override suspend fun getRepresentativePicture(categoryId: Any): CategorizedPicture? {
        TODO("Not yet implemented")
    }

    override suspend fun putPicture(picture: Uri, category: Category): CategorizedPicture {
        TODO("Not yet implemented")
    }

    override suspend fun getCategoryById(categoryId: Any): Category? {
        TODO("Not yet implemented")
    }

    override suspend fun getCategoryByName(categoryName: String): Collection<Category> {
        TODO("Not yet implemented")
    }

    override suspend fun putCategory(categoryName: String): Category {
        TODO("Not yet implemented")
    }

    override suspend fun getCategories(): Set<Category> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllPictures(category: Category): Set<CategorizedPicture> {
        TODO("Not yet implemented")
    }

    override suspend fun removeCategory(category: Category) {
        TODO("Not yet implemented")
    }

    override suspend fun removePicture(picture: CategorizedPicture) {
        TODO("Not yet implemented")
    }

    override suspend fun putDataset(name: String, categories: Set<Category>): Dataset {
        TODO("Not yet implemented")
    }

    override suspend fun getDatasetById(id: Any): Dataset? {
        TODO("Not yet implemented")
    }

    override suspend fun getDatasetByName(datasetName: String): Collection<Dataset> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteDataset(id: Any) {
        TODO("Not yet implemented")
    }

    override suspend fun putRepresentativePicture(picture: Uri, category: Category) {
        TODO("Not yet implemented")
    }

    override suspend fun getDatasets(): Set<Dataset> {
        TODO("Not yet implemented")
    }

    override suspend fun getDatasetNames(): Collection<String> {
        TODO("Not yet implemented")
    }

    override suspend fun getDatasetIds(): Set<Any> {
        TODO("Not yet implemented")
    }

    override suspend fun removeCategoryFromDataset(dataset: Dataset, category: Category): Dataset {
        TODO("Not yet implemented")
    }

    override suspend fun editDatasetName(dataset: Dataset, newName: String): Dataset {
        TODO("Not yet implemented")
    }
}