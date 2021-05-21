package com.github.HumanLearning2021.HumanLearningApp.model

import android.net.Uri
import com.google.firebase.auth.FirebaseUser
import java.util.*

/**
 * A class representing a dummy data set Interface
 * Categories are uniquely defined by their name
 */
class DummyDatabaseService internal constructor() : DatabaseService {
    private val pictures = InMemoryRepository<CategorizedPicture>()
    private val categories = InMemoryRepository<Category>()
    private val datasets = InMemoryRepository<Dataset>()
    private val representativePictures: MutableMap<String, CategorizedPicture> = mutableMapOf()
    private val users = InMemoryRepository<User>()
    private val statistics = InMemoryRepository<Statistic>()

    private suspend fun requireCategoryPresent(category: Category) {
        categories.getById(category.id) ?: throw DatabaseService.NotFoundException(category.id)
    }

    private suspend fun requireDatasetPresent(dataset: Dataset) {
        datasets.getById(dataset.id) ?: throw DatabaseService.NotFoundException(dataset.id)
    }

    private suspend fun requirePicturePresent(picture: CategorizedPicture) {
        pictures.getById(picture.id) ?: throw DatabaseService.NotFoundException(picture.id)
    }

    override suspend fun getPicture(category: Category): CategorizedPicture? {
        requireCategoryPresent(category)

        return pictures.getIds().map { pictures.getById(it) }
            .find { it?.category?.id == category.id }
    }

    override suspend fun getPicture(pictureId: Id): CategorizedPicture? =
        pictures.getById(pictureId)

    override suspend fun getPictureIds(category: Category): List<Id> {
        requireCategoryPresent(category)
        return pictures.getIds().filter { pictures.getById(it)?.category?.id == category.id }
    }

    override suspend fun getRepresentativePicture(categoryId: Id): CategorizedPicture? {
        return representativePictures[categoryId]
    }

    override suspend fun putPicture(picture: Uri, category: Category): CategorizedPicture {
        requireCategoryPresent(category)

        val addedPicture = CategorizedPicture("", category, picture)
        val id = pictures.createWith { addedPicture.copy(id = it) }

        return addedPicture.copy(id = id)
    }

    override suspend fun getCategory(id: Id): Category? {
        return categories.getById(id)
    }

    override suspend fun putCategory(categoryName: String): Category {
        val category = Category(categoryName, categoryName)
        categories.update(categoryName, category)
        return category
    }

    override suspend fun getCategories(): Set<Category> {
        return categories.getIds().mapNotNull { categories.getById(it) }.toSet()
    }

    override suspend fun getAllPictures(category: Category): Set<CategorizedPicture> {
        requireCategoryPresent(category)

        return getPictureIds(category).mapNotNull { pictures.getById(it) }.toSet()
    }

    override suspend fun removeCategory(category: Category) {
        requireCategoryPresent(category)
        datasets.updateAll {
            it.copy(categories = it.categories - category)
        }
        categories.delete(category.id)
    }

    override suspend fun removePicture(picture: CategorizedPicture) {
        if (!pictures.delete(picture.id)) throw DatabaseService.NotFoundException(picture.id)
    }

    override suspend fun putDataset(name: String, categories: Set<Category>): Dataset {
        val dataset = Dataset(name, name, categories)
        datasets.update(name, dataset)
        return dataset
    }

    override suspend fun getDataset(id: Id): Dataset? =
        datasets.getById(id)

    override suspend fun deleteDataset(id: Id) {
        if (!datasets.delete(id)) throw DatabaseService.NotFoundException(id)
    }

    override suspend fun putRepresentativePicture(picture: Uri, category: Category) {
        requireCategoryPresent(category)
        representativePictures[category.id] =
            CategorizedPicture("${UUID.randomUUID()}", category, picture)
    }

    override suspend fun putRepresentativePicture(picture: CategorizedPicture) {
        require(picture is CategorizedPicture)
        requirePicturePresent(picture)
        putRepresentativePicture(picture.picture, picture.category)
        pictures.delete(picture.id)
    }

    override suspend fun getDatasets(): Set<Dataset> {
        return datasets.getIds().mapNotNull { datasets.getById(it) }.toSet()
    }

    override suspend fun removeCategoryFromDataset(
        dataset: Dataset,
        category: Category
    ): Dataset {
        requireDatasetPresent(dataset)
        return dataset.copy(categories = dataset.categories - category).also {
            datasets.update(it.id, it)
        }
    }

    override suspend fun editDatasetName(dataset: Dataset, newName: String): Dataset {
        requireDatasetPresent(dataset)
        return dataset.copy(name = newName).also { datasets.update(dataset.id, it) }
    }

    override suspend fun addCategoryToDataset(dataset: Dataset, category: Category): Dataset {
        requireCategoryPresent(category)
        requireDatasetPresent(dataset)
        return if (dataset.categories.contains(category)) {
            dataset
        } else {
            dataset.copy(categories = dataset.categories + category).also {
                datasets.update(it.id, it)
            }
        }
    }

    override suspend fun updateUser(firebaseUser: FirebaseUser) =
        User(
            type = User.Type.FIREBASE,
            uid = firebaseUser.uid,
            email = firebaseUser.email,
            displayName = firebaseUser.displayName,
            isAdmin = false
        ).also { users.update(it.id.toString(), it) }

    override suspend fun setAdminAccess(firebaseUser: FirebaseUser, adminAccess: Boolean): User {
        val type = User.Type.FIREBASE
        val uid = firebaseUser.uid
        return User(
            type = type,
            uid = uid,
            email = firebaseUser.email,
            displayName = firebaseUser.displayName,
            isAdmin = adminAccess
        ).also { users.update(it.id.toString(), it) }
    }

    override suspend fun checkIsAdmin(user: User): Boolean {
        return user.isAdmin
    }

    override suspend fun getUser(type: User.Type, uid: String) =
        users.getById(User.Id(uid, type).toString())

    override suspend fun getStatistic(userId: User.Id, datasetId: Id): Statistic? =
        statistics.getById(Statistic.Id(userId, datasetId).toString())

    override suspend fun putStatistic(statistic: Statistic) {
        statistics.update(statistic.id.toString(), statistic)
    }
}
