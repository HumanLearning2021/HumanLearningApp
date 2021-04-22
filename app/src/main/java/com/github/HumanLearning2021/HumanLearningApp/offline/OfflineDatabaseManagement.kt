package com.github.HumanLearning2021.HumanLearningApp.offline

import android.net.Uri
import com.github.HumanLearning2021.HumanLearningApp.model.CategorizedPicture
import com.github.HumanLearning2021.HumanLearningApp.model.Category
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.Dataset
import java.lang.Exception
import java.lang.IllegalArgumentException

class OfflineDatabaseManagement (
    private val databaseService: OfflineDatabaseService
): DatabaseManagement {

    override suspend fun getPicture(category: Category): CategorizedPicture? {
        require(category is OfflineCategory)
        return try {
            databaseService.getPicture(category)
        } catch (e: IllegalArgumentException) {
            throw e
        }
    }

    override suspend fun getPicture(pictureId: Any): CategorizedPicture? {
        require(pictureId is String)
        return databaseService.getPicture(pictureId)
    }

    override suspend fun getPictureIds(category: Category): List<String> {
        require(category is OfflineCategory)
        return try {
            databaseService.getPictureIds(category)
        } catch (e: IllegalArgumentException) {
            throw e
        }
    }

    override suspend fun getRepresentativePicture(categoryId: Any): CategorizedPicture? {
        require(categoryId is String)
        return databaseService.getRepresentativePicture(categoryId)
    }

    override suspend fun putPicture(picture: Uri, category: Category): CategorizedPicture {
        require(category is OfflineCategory)
        return try {
            databaseService.putPicture(picture, category)
        } catch (e: IllegalArgumentException) {
            throw e
        }
    }

    override suspend fun getCategoryById(categoryId: Any): Category? {
        require(categoryId is String)
        return databaseService.getCategory(categoryId)
    }

    override suspend fun getCategoryByName(categoryName: String): Collection<Category> {
        val categories = databaseService.getCategories()
        val res: MutableSet<OfflineCategory> = mutableSetOf()
        for (c in categories) {
            if (c.name == categoryName) {
                res.add(c)
            }
        }
        return res.toSet()
    }

    override suspend fun putCategory(categoryName: String): Category {
        return databaseService.putCategory(categoryName)
    }

    override suspend fun getCategories(): Set<Category> {
        return databaseService.getCategories()
    }

    override suspend fun getAllPictures(category: Category): Set<CategorizedPicture> {
        require(category is OfflineCategory)
        return try {
            databaseService.getAllPictures(category)
        } catch (e: IllegalArgumentException) {
            throw e
        }
    }

    override suspend fun removeCategory(category: Category) {
        require(category is OfflineCategory)
        try {
            databaseService.removeCategory(category)
        } catch (e: Exception) {
            //do nothing since this means that the category is not in the database which is the same as having it removed
        }
    }

    override suspend fun removePicture(picture: CategorizedPicture) {
        require(picture is OfflineCategorizedPicture)
        try {
            databaseService.removePicture(picture)
        } catch (e: Exception) {
            //do nothing since this means that the picture is not in the database which is the same as having it removed
        }
    }

    override suspend fun putDataset(name: String, categories: Set<Category>): Dataset {
        return databaseService.putDataset(name, categories)
    }

    override suspend fun getDatasetById(id: Any): Dataset? {
        require(id is String)
        return databaseService.getDataset(id)
    }

    override suspend fun getDatasetByName(datasetName: String): Collection<Dataset> {
        val datasets = databaseService.getDatasets()
        val res: MutableSet<OfflineDataset> = mutableSetOf()
        for (d in datasets) {
            if (d.name == datasetName) {
                res.add(d as OfflineDataset)
            }
        }
        return res.toSet()
    }

    override suspend fun deleteDataset(id: Any) {
        require(id is String)
        try {
            databaseService.deleteDataset(id)
        } catch (e: IllegalArgumentException) {
            //do nothing since this means that the dataset is not in the database which is the same as having it removed
        }
    }

    override suspend fun putRepresentativePicture(picture: Uri, category: Category) {
        require(category is OfflineCategory)
        try {
            databaseService.putRepresentativePicture(picture, category)
        } catch (e: IllegalArgumentException) {
            throw e
        }
    }

    override suspend fun getDatasets(): Set<Dataset> {
        return databaseService.getDatasets()
    }

    override suspend fun getDatasetNames(): Collection<String> {
        val datasets = databaseService.getDatasets()
        val res: MutableSet<String> = mutableSetOf()
        for (d in datasets) {
            res.add(d.name)
        }
        return res.toSet()
    }

    override suspend fun getDatasetIds(): Set<Any> {
        val datasets = databaseService.getDatasets()
        val res: MutableSet<String> = mutableSetOf()
        for (d in datasets) {
            res.add(d.id as String)
        }
        return res.toSet()
    }

    override suspend fun removeCategoryFromDataset(dataset: Dataset, category: Category): Dataset {
        require(dataset is OfflineDataset)
        require(category is OfflineCategory)
        return try {
            databaseService.removeCategoryFromDataset(dataset, category)
        } catch (e: IllegalArgumentException) {
            val newCats = mutableListOf<OfflineCategory>()
            newCats.addAll(dataset.categories as Set<OfflineCategory>)
            newCats.remove(category)
            return OfflineDataset(dataset.id, dataset.name, newCats.toSet())
        }
    }

    override suspend fun editDatasetName(dataset: Dataset, newName: String): Dataset {
        require(dataset is OfflineDataset)
        return try {
            databaseService.editDatasetName(dataset, newName)
        } catch (e: IllegalArgumentException) {
            throw e
        }
    }

    override suspend fun addCategoryToDataset(dataset: Dataset, category: Category): Dataset {
        require(dataset is OfflineDataset)
        require(category is OfflineCategory)
        return try {
            databaseService.addCategoryToDataset(dataset, category)
        } catch (e: IllegalArgumentException) {
            throw e
        }
    }
}