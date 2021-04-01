package com.github.HumanLearning2021.HumanLearningApp.model

import android.net.Uri
import com.google.common.collect.ImmutableSet

/**
 * Dummy implementation of a database manager
 */
class DummyDatabaseManagement: DatabaseManagement {
    private val databaseService: DummyDatabaseService = DummyDatabaseService()

    override suspend fun getPicture(category: Category): CategorizedPicture? {
        return databaseService.getPicture(category)
    }

    override suspend fun putPicture(picture: Uri, category: Category): CategorizedPicture {
       return databaseService.putPicture(picture, category)
    }

    override suspend fun getCategory(categoryId: Any): Category? {
        return databaseService.getCategory(categoryId)
    }

    override suspend fun getCategory(categoryName: String): Collection<Category> {
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

    override suspend fun getRepresentativePicture(category: Category): CategorizedPicture? {
        return getRepresentativePicture(category)
    }

    override suspend fun getAllPictures(category: Category): Set<CategorizedPicture> {
        return databaseService.getAllPictures(category)
    }

    override suspend fun removeCategory(category: Category) {
        databaseService.removeCategory(category)
    }

    override suspend fun removePicture(picture: CategorizedPicture) {
        databaseService.removePicture(picture)
    }

    override suspend fun putDataset(name: String, categories: Set<Category>): Dataset {
        return databaseService.putDataset(name, categories)
    }

    override suspend fun getDataset(id: Any): Dataset? {
        return databaseService.getDataset(id)
    }

    override suspend fun getDataset(datasetName: String): Collection<Dataset>? {
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
        databaseService.deleteDataset(id)
    }

    override suspend fun putRepresentativePicture(picture: Uri, category: Category) {
        databaseService.putRepresentativePicture(picture, category)
    }

    override suspend fun getDatasets(): Set<Dataset> {
        return databaseService.getDatasets()
    }

    override suspend fun putRepresentativePicture(picture: CategorizedPicture) {
        databaseService.putRepresentativePicture((picture as DummyCategorizedPicture).picture, picture.category)
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