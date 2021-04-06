package com.github.HumanLearning2021.HumanLearningApp.model

import android.net.Uri
import android.os.Parcelable
import com.google.common.collect.ImmutableSet
import kotlinx.parcelize.Parcelize
import java.lang.IllegalArgumentException

/**
 * Dummy implementation of a database manager
 * Dataset & category names and ids are equivalent
 */
data class DummyDatabaseManagement(val databaseService: DummyDatabaseService): DatabaseManagement {
    companion object {
        val staticDummyDatabaseManagement = DummyDatabaseManagement(DummyDatabaseService())
    }

    override suspend fun getPicture(category: Category): CategorizedPicture? {
        return try {
            databaseService.getPicture(category)
        } catch (e: IllegalArgumentException) {
            throw e
        }
    }

    override suspend fun getRepresentativePicture(categoryId: Any): CategorizedPicture? {
        return databaseService.getRepresentativePicture(categoryId)
    }

    override suspend fun putPicture(picture: Uri, category: Category): CategorizedPicture {
       return try {
           databaseService.putPicture(picture, category)
       } catch (e: IllegalArgumentException) {
           throw e
       }
    }

    override suspend fun getCategoryById(categoryId: Any): Category? {
        return databaseService.getCategory(categoryId)
    }

    override suspend fun getCategoryByName(categoryName: String): Collection<Category> {
        val categories = databaseService.getCategories()
        val res: MutableSet<Category> = mutableSetOf()
        for (c in categories) {
            if (c.name == categoryName) {
                res.add(c)
            }
        }
        return ImmutableSet.copyOf(res)
    }

    override suspend fun putCategory(categoryName: String): Category {
        return databaseService.putCategory(categoryName)
    }

    override suspend fun getCategories(): Set<Category> {
        return databaseService.getCategories()
    }

    override suspend fun getAllPictures(category: Category): Set<CategorizedPicture> {
        return try {
            databaseService.getAllPictures(category)
        } catch (e: IllegalArgumentException) {
            throw e
        }
    }

    override suspend fun removeCategory(category: Category) {
        try {
            databaseService.removeCategory(category)
        } catch (e: IllegalArgumentException) {
            throw e
        }
    }

    override suspend fun removePicture(picture: CategorizedPicture) {
        try {
            databaseService.removePicture(picture)
        } catch (e: IllegalArgumentException) {
            throw e
        }
    }

    override suspend fun putDataset(name: String, categories: Set<Category>): Dataset {
        return databaseService.putDataset(name, categories)
    }

    override suspend fun getDatasetById(id: Any): Dataset? {
        return databaseService.getDataset(id)
    }

    override suspend fun getDatasetByName(datasetName: String): Collection<Dataset> {
        val datasets = databaseService.getDatasets()
        val res: MutableSet<Dataset> = mutableSetOf()
        for (d in datasets) {
            if (d.name == datasetName) {
                res.add(d)
            }
        }
        return ImmutableSet.copyOf(res)
    }

    override suspend fun deleteDataset(id: Any) {
        try {
            databaseService.deleteDataset(id)
        } catch (e: IllegalArgumentException) {
            throw e
        }
    }

    override suspend fun putRepresentativePicture(picture: Uri, category: Category) {
        try {
            databaseService.putRepresentativePicture(picture, category)
        } catch (e: IllegalArgumentException) {
            throw e
        }
    }

    override suspend fun putRepresentativePicture(picture: CategorizedPicture) {
        try {
            databaseService.putRepresentativePicture((picture as DummyCategorizedPicture).picture, picture.category)
        } catch (e: IllegalArgumentException) {
            throw e
        }
    }

    override suspend fun getDatasets(): Set<Dataset> {
        return databaseService.getDatasets()
    }

    override suspend fun getDatasetNames(): Collection<String> {
        val datasets = databaseService.getDatasets()
        val res: ArrayList<String> = arrayListOf()
        for (d in datasets) {
            res.add(d.name)
        }
        return res
    }

    override suspend fun getDatasetIds(): Set<Any> {
        val datasets = databaseService.getDatasets()
        val res: ArrayList<String> = arrayListOf()
        for (d in datasets) {
            res.add(d.id as String)
        }
        return ImmutableSet.copyOf(res)
    }
}