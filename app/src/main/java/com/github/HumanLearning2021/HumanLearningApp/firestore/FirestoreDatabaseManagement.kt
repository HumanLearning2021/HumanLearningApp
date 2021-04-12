package com.github.HumanLearning2021.HumanLearningApp.firestore

import android.net.Uri
import com.github.HumanLearning2021.HumanLearningApp.model.CategorizedPicture
import com.github.HumanLearning2021.HumanLearningApp.model.Category
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.Dataset
import com.google.firebase.FirebaseApp
import java.lang.Exception
import java.lang.IllegalArgumentException

class FirestoreDatabaseManagement(dbName: String, app: FirebaseApp? = null): DatabaseManagement {

    private val databaseService: FirestoreDatabaseService = FirestoreDatabaseService(dbName, app)

    companion object {
        val scratchFirestoreDatabase = FirestoreDatabaseManagement("scratch")
    }

    override suspend fun getPicture(category: Category): CategorizedPicture? {
        require(category is FirestoreCategory)
        return try {
            databaseService.getPicture(category)
        } catch (e: IllegalArgumentException) {
            throw e
        }
    }

    override suspend fun getRepresentativePicture(categoryId: Any): FirestoreCategorizedPicture? {
        require(categoryId is String)
        return databaseService.getRepresentativePicture(categoryId)
    }

    override suspend fun putPicture(picture: Uri, category: Category): FirestoreCategorizedPicture {
        require(category is FirestoreCategory)
        return try {
            databaseService.putPicture(picture, category)
        } catch (e: IllegalArgumentException) {
           throw e
        }
    }

    override suspend fun getCategoryById(categoryId: Any): FirestoreCategory? {
        require(categoryId is String)
        return databaseService.getCategory(categoryId)
    }

    override suspend fun getCategoryByName(categoryName: String): Collection<FirestoreCategory> {
        val categories = databaseService.getCategories()
        val res: MutableSet<FirestoreCategory> = mutableSetOf()
        for (c in categories) {
            if (c.name == categoryName) {
                res.add(c)
            }
        }
        return res.toSet()
    }

    override suspend fun putCategory(categoryName: String): FirestoreCategory {
        return databaseService.putCategory(categoryName)
    }

    override suspend fun getCategories(): Set<FirestoreCategory> {
        return databaseService.getCategories()
    }

    override suspend fun getAllPictures(category: Category): Set<FirestoreCategorizedPicture> {
        require(category is FirestoreCategory)
        return try {
            databaseService.getAllPictures(category)
        } catch (e: IllegalArgumentException) {
            throw e
        }
    }

    override suspend fun removeCategory(category: Category) {
        require(category is FirestoreCategory)
        try {
            databaseService.removeCategory(category)
        } catch (e: Exception) {
            //do nothing since this means that the category is not in the database which is the same as having it removed
        }
    }

    override suspend fun removePicture(picture: CategorizedPicture) {
        require(picture is FirestoreCategorizedPicture)
        try {
            databaseService.removePicture(picture)
        } catch (e: Exception) {
            //do nothing since this means that the picture is not in the database which is the same as having it removed
        }
    }

    override suspend fun putDataset(name: String, categories: Set<Category>): FirestoreDataset {
        return databaseService.putDataset(name, categories)
    }

    override suspend fun getDatasetById(id: Any): Dataset? {
        require(id is String)
        return databaseService.getDataset(id)
    }

    override suspend fun getDatasetByName(datasetName: String): Collection<FirestoreDataset> {
        val datasets = databaseService.getDatasets()
        val res: MutableSet<FirestoreDataset> = mutableSetOf()
        for (d in datasets) {
            if (d.name == datasetName) {
                res.add(d)
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
        require(category is FirestoreCategory)
        try {
            databaseService.putRepresentativePicture(picture, category)
        } catch (e: IllegalArgumentException) {
            throw e
        }
    }

    override suspend fun getDatasets(): Set<FirestoreDataset> {
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
            res.add(d.id)
        }
        return res.toSet()
    }

    override suspend fun removeCategoryFromDataset(dataset: Dataset, category: Category): FirestoreDataset {
        require(dataset is FirestoreDataset)
        require(category is FirestoreCategory)
        return try {
            databaseService.removeCategoryFromDataset(dataset, category)
        } catch (e: IllegalArgumentException) {
            val newCats = mutableListOf<FirestoreCategory>()
            newCats.addAll(dataset.categories)
            newCats.remove(category)
            return FirestoreDataset(dataset.path, dataset.id, dataset.name, newCats.toSet())
        }
    }

    override suspend fun editDatasetName(dataset: Dataset, newName: String): Dataset {
        require(dataset is FirestoreDataset)
        return try {
            databaseService.editDatasetName(dataset, newName)
        } catch (e: IllegalArgumentException) {
            throw e
        }
    }

    override suspend fun addCategoryToDataset(dataset: Dataset, category: Category): Dataset {
        require(dataset is FirestoreDataset)
        require(category is FirestoreCategory)
        return try {
            databaseService.addCategoryToDataset(dataset, category)
        } catch (e: IllegalArgumentException) {
            throw e
        }
    }
}