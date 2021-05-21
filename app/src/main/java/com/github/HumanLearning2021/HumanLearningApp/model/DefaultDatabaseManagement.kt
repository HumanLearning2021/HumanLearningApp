package com.github.HumanLearning2021.HumanLearningApp.model

import android.net.Uri

class DefaultDatabaseManagement internal constructor(
    private val databaseService: DatabaseService
) : DatabaseManagement {
    override suspend fun getPicture(category: Category): CategorizedPicture? {
        return try {
            databaseService.getPicture(category)
        } catch (e: DatabaseService.NotFoundException) {
            throw e
        }
    }

    override suspend fun getPicture(pictureId: Id): CategorizedPicture? {
        return databaseService.getPicture(pictureId)
    }

    override suspend fun getPictureIds(category: Category): List<Id> {
        return try {
            databaseService.getPictureIds(category)
        } catch (e: DatabaseService.NotFoundException) {
            throw e
        }
    }

    override suspend fun getRepresentativePicture(categoryId: Id): CategorizedPicture? {
        return databaseService.getRepresentativePicture(categoryId)
    }

    override suspend fun putPicture(picture: Uri, category: Category): CategorizedPicture {
        return try {
            databaseService.putPicture(picture, category)
        } catch (e: DatabaseService.NotFoundException) {
            throw e
        }
    }

    override suspend fun getCategoryById(categoryId: Id): Category? {
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
        return res.toSet()
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
        } catch (e: DatabaseService.NotFoundException) {
            throw e
        }
    }

    override suspend fun removeCategory(category: Category) {
        try {
            databaseService.removeCategory(category)
        } catch (e: DatabaseService.NotFoundException) {
            //do nothing since this means that the category is not in the database which is the same as having it removed
        }
    }

    override suspend fun removePicture(picture: CategorizedPicture) {
        try {
            databaseService.removePicture(picture)
        } catch (e: DatabaseService.NotFoundException) {
            //do nothing since this means that the picture is not in the database which is the same as having it removed
        }
    }

    override suspend fun putDataset(name: String, categories: Set<Category>): Dataset {
        return databaseService.putDataset(name, categories)
    }

    override suspend fun getDatasetById(id: Id): Dataset? {
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
        return res.toSet()
    }

    override suspend fun deleteDataset(id: Id) {
        try {
            databaseService.deleteDataset(id)
        } catch (e: DatabaseService.NotFoundException) {
            //do nothing since this means that the dataset is not in the database which is the same as having it removed
        }
    }

    override suspend fun putRepresentativePicture(picture: Uri, category: Category) {
        try {
            databaseService.putRepresentativePicture(picture, category)
        } catch (e: DatabaseService.NotFoundException) {
            throw e
        }
    }

    /**
     * Sets a categorized picture as the representative picture of the category it is assigned to,
     * removing it from the pictures of the category in the process.
     *
     * @param picture - the categorized picture to set as representative picture
     * @throws DatabaseService.NotFoundException if the underlying database does not contain the specified picture
     */
    override suspend fun putRepresentativePicture(picture: CategorizedPicture) {
        databaseService.putRepresentativePicture(picture)
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

    override suspend fun getDatasetIds(): Set<Id> {
        val datasets = databaseService.getDatasets()
        val res: MutableSet<String> = mutableSetOf()
        for (d in datasets) {
            res.add(d.id)
        }
        return res.toSet()
    }

    override suspend fun removeCategoryFromDataset(dataset: Dataset, category: Category): Dataset {
        return try {
            databaseService.removeCategoryFromDataset(dataset, category)
        } catch (e: DatabaseService.NotFoundException) {
            throw e
        }
    }

    override suspend fun editDatasetName(dataset: Dataset, newName: String): Dataset {
        return try {
            databaseService.editDatasetName(dataset, newName)
        } catch (e: DatabaseService.NotFoundException) {
            throw e
        }
    }

    override suspend fun addCategoryToDataset(dataset: Dataset, category: Category): Dataset {
        return try {
            databaseService.addCategoryToDataset(dataset, category)
        } catch (e: DatabaseService.NotFoundException) {
            throw e
        }
    }

    override suspend fun countOccurrence(user: User.Id, dataset: Id, event: Event) {
        (databaseService.getStatistic(user, dataset) ?: Statistic(
            Statistic.Id(
                user,
                dataset
            ),
            mapOf()
        )).let { stat ->
            stat.copy(occurrences = stat.occurrences.let {
                it + (event to it.getOrDefault(
                    event,
                    0
                ) + 1)
            })
        }.also { stat ->
            databaseService.putStatistic(stat)
        }
    }
}
 
