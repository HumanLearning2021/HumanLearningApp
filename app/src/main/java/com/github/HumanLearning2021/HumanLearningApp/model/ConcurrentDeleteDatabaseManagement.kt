package com.github.HumanLearning2021.HumanLearningApp.model

import kotlinx.parcelize.Parcelize

class ConcurrentDeleteDatabaseManagement(private val db: DatabaseManagement) :
    DatabaseManagement by db {
    @Parcelize
    data class GhostDataset(
        override val id: Id,
        override val name: String,
        override val categories: Set<Category>,
    ) : Dataset

    private fun (Dataset).toGhost() =
        GhostDataset(id, name, categories)

    override suspend fun addCategoryToDataset(dataset: Dataset, category: Category): Dataset =
        if (dataset is GhostDataset)
            dataset.copy(categories = dataset.categories.plusElement(category))
        else
            try {
                db.addCategoryToDataset(dataset, category)
            } catch (e: DatabaseService.NotFoundException) {
                addCategoryToDataset(dataset.toGhost(), category)
            }

    override suspend fun removeCategoryFromDataset(dataset: Dataset, category: Category): Dataset =
        if (dataset is GhostDataset)
            dataset.copy(categories = dataset.categories.minusElement(category))
        else
            try {
                db.removeCategoryFromDataset(dataset, category)
            } catch (e: DatabaseService.NotFoundException) {
                removeCategoryFromDataset(dataset.toGhost(), category)
            }

    override suspend fun editDatasetName(dataset: Dataset, newName: String): Dataset =
        if (dataset is GhostDataset)
            dataset.copy(name = newName)
        else
            try {
                db.editDatasetName(dataset, newName)
            } catch (e: DatabaseService.NotFoundException) {
                editDatasetName(dataset.toGhost(), newName)
            }
}