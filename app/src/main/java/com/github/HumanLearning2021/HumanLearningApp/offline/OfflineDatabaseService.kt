package com.github.HumanLearning2021.HumanLearningApp.offline

import android.content.Context
import android.net.Uri
import com.github.HumanLearning2021.HumanLearningApp.model.*
import com.github.HumanLearning2021.HumanLearningApp.offline.OfflineConverters.fromCategory
import com.github.HumanLearning2021.HumanLearningApp.offline.OfflineConverters.fromDataset
import com.github.HumanLearning2021.HumanLearningApp.offline.OfflineConverters.fromPicture
import com.github.HumanLearning2021.HumanLearningApp.offline.OfflineConverters.fromUser
import com.github.HumanLearning2021.HumanLearningApp.room.*
import com.google.firebase.auth.FirebaseUser
import java.util.*

class OfflineDatabaseService internal constructor(
    val dbName: String,
    val context: Context,
    val room: RoomOfflineDatabase
) : DatabaseService {

    private val pictureRepository: PictureRepository = PictureRepository(dbName, context)
    private val databaseDao: DatabaseDao = room.databaseDao()
    private val datasetDao: DatasetDao = room.datasetDao()
    private val categoryDao: CategoryDao = room.categoryDao()
    private val userDao: UserDao = room.userDao()

    private fun getID() = "${UUID.randomUUID()}"

    /**
     * A function to retrieve a picture from the database given a category
     *
     * @param category the category of the image to be retrieved
     * @return a CategorizedPicture from the desired category. Null if no picture of the desired
     * category is present in the database or if the category is not present
     */
    override suspend fun getPicture(category: Category): OfflineCategorizedPicture? {
        val pics =
            categoryDao.loadAllPictures(category.id) ?: throw DatabaseService.NotFoundException(
                category.id
            )
        val randomId = pics.pictures.map { p -> p.pictureId }.random()
        return fromPicture(categoryDao.loadPicture(randomId)!!, categoryDao)
    }

    override suspend fun getPicture(pictureId: Id): OfflineCategorizedPicture? {
        val pic = categoryDao.loadPicture(pictureId) ?: return null
        return fromPicture(pic, categoryDao)
    }

    /**
     * A function to retrieve the ids of all the pictures from the database given a category
     *
     * @param category the category of image to be retrieved
     * @return a List of ids. Can be empty if no pictures where found or if the category is not contained in the database
     */
    override suspend fun getPictureIds(category: Category): List<String> {
        val pics =
            categoryDao.loadAllPictures(category.id) ?: throw DatabaseService.NotFoundException(
                category.id
            )
        return pics.pictures.map { p -> p.pictureId }
    }

    override suspend fun getRepresentativePicture(categoryId: Id): OfflineCategorizedPicture? {
        val cat = categoryDao.loadRepresentativePicture(categoryId) ?: return null
        return fromPicture(cat, categoryDao)
    }

    override suspend fun putPicture(picture: Uri, category: Category): OfflineCategorizedPicture {
        val cat = categoryDao.loadById(category.id) ?: throw DatabaseService.NotFoundException(
            category.id
        )
        val pic = RoomPicture(getID(), picture, cat.categoryId)
        val ref = RoomDatabasePicturesCrossRef(dbName, pic.pictureId)
        pictureRepository.savePicture(picture)
        categoryDao.insertAll(pic)
        databaseDao.insertAll(ref)
        return fromPicture(pic, categoryDao)
    }

    override suspend fun getCategory(id: Id): OfflineCategory? {
        val cat = categoryDao.loadById(id) ?: return null
        return fromCategory(cat)
    }

    override suspend fun putCategory(categoryName: String): OfflineCategory {
        val cat = RoomCategory(getID(), categoryName)
        val ref = RoomDatabaseCategoriesCrossRef(dbName, cat.categoryId)
        categoryDao.insertAll(cat)
        databaseDao.insertAll(ref)
        return fromCategory(cat)
    }

    override suspend fun getCategories(): Set<OfflineCategory> {
        return databaseDao.loadByName(dbName)!!.categories.map { c -> fromCategory(c) }.toSet()
    }

    /**
     * Retrieves all the pictures categorized with the specified category
     *
     * @param category - the category whose pictures we want to retrieve
     * @return the pictures categorized with the specified category, empty if the category is not contained in the dataset
     */
    override suspend fun getAllPictures(category: Category): Set<OfflineCategorizedPicture> {
        val cats =
            categoryDao.loadAllPictures(category.id) ?: throw DatabaseService.NotFoundException(
                category.id
            )
        return cats.pictures.map { p -> fromPicture(p, categoryDao) }.toSet()
    }

    /**
     * Remove the category from the database and from all the datasets contained in this database and using this category
     *
     * @param category - the category to remove from the database
     */
    override suspend fun removeCategory(category: Category) {
        categoryDao.delete(fromCategory(category))
        val dbRef = RoomDatabaseCategoriesCrossRef(dbName, category.id)
        val dsRefs = datasetDao.loadAll(category.id)
        databaseDao.delete(dbRef)
        datasetDao.delete(*dsRefs.toTypedArray())
    }

    /**
     * Removes the corresponding picture from the database
     *
     * @param picture - the picture to remove from the database
     */
    override suspend fun removePicture(picture: CategorizedPicture) {
        val pic = categoryDao.loadPicture(picture.id)
        if (pic != null) {
            val ref = RoomDatabasePicturesCrossRef(dbName, pic.pictureId)
            pictureRepository.deletePicture(pic.pictureId)
            categoryDao.delete(pic)
            databaseDao.delete(ref)
        }
    }

    override suspend fun putDataset(name: String, categories: Set<Category>): OfflineDataset {
        val id = getID()
        val ds = RoomDatasetWithoutCategories(id, name)
        val dsRefs = mutableListOf<RoomDatasetCategoriesCrossRef>()
        for (c in categories) {
            dsRefs.add(RoomDatasetCategoriesCrossRef(id, c.id))
        }
        val dbRef = RoomDatabaseDatasetsCrossRef(dbName, id)
        val cats = categories.map { c -> fromCategory(c) }
        datasetDao.insertAll(ds)
        datasetDao.insertAll(*dsRefs.toTypedArray())
        databaseDao.insertAll(dbRef)
        return fromDataset(RoomDataset(ds, cats))
    }

    override suspend fun getDataset(id: Id): OfflineDataset? {
        val ds = datasetDao.loadById(id) ?: return null
        return fromDataset(ds)
    }

    /**
     * Deletes the specified dataset from the database
     *
     * @param id - the name of the dataset to delete
     */
    override suspend fun deleteDataset(id: Id) {
        val ds = datasetDao.loadById(id)
        if (ds != null) {
            val dsRefs = datasetDao.loadAll(ds.datasetWithoutCategories.datasetId)
            val dbRef = RoomDatabaseDatasetsCrossRef(dbName, ds.datasetWithoutCategories.datasetId)
            datasetDao.delete(ds.datasetWithoutCategories)
            datasetDao.delete(*dsRefs.toTypedArray())
            databaseDao.delete(dbRef)
        }
    }

    override suspend fun putRepresentativePicture(picture: Uri, category: Category) {
        val cat = categoryDao.loadById(category.id) ?: throw DatabaseService.NotFoundException(
            category.id
        )
        categoryDao.insertAll(RoomUnlinkedRepresentativePicture(getID(), picture, cat.categoryId))
    }

    override suspend fun putRepresentativePicture(picture: CategorizedPicture) {
        require(picture is OfflineCategorizedPicture)
        putRepresentativePicture(picture.picture, picture.category)
    }

    override suspend fun getDatasets(): Set<OfflineDataset> {
        return databaseDao.loadByName(dbName)?.datasets?.map { d ->
            fromDataset(
                datasetDao.loadById(
                    d.datasetId
                )!!
            )
        }?.toSet() ?: setOf()
    }

    override suspend fun removeCategoryFromDataset(
        dataset: Dataset,
        category: Category
    ): OfflineDataset {
        datasetDao.delete(RoomDatasetCategoriesCrossRef(dataset.id, category.id))
        val ds =
            datasetDao.loadById(dataset.id) ?: throw DatabaseService.NotFoundException(dataset.id)
        return fromDataset(ds)
    }

    override suspend fun editDatasetName(dataset: Dataset, newName: String): OfflineDataset {
        val ds =
            datasetDao.loadById(dataset.id) ?: throw DatabaseService.NotFoundException(dataset.id)
        val updatedDs = RoomDatasetWithoutCategories(ds.datasetWithoutCategories.datasetId, newName)
        datasetDao.update(updatedDs)
        return fromDataset(RoomDataset(updatedDs, ds.categories))
    }

    override suspend fun addCategoryToDataset(
        dataset: Dataset,
        category: Category
    ): OfflineDataset {
        val ds =
            datasetDao.loadById(dataset.id) ?: throw DatabaseService.NotFoundException(dataset.id)
        val cat = categoryDao.loadById(category.id) ?: throw DatabaseService.NotFoundException(
            category.id
        )
        datasetDao.insertAll(
            RoomDatasetCategoriesCrossRef(
                ds.datasetWithoutCategories.datasetId,
                cat.categoryId
            )
        )
        val updatedCats = ds.categories.toMutableList()
        updatedCats.add(cat)
        return fromDataset(RoomDataset(ds.datasetWithoutCategories, updatedCats))
    }

    override suspend fun updateUser(firebaseUser: FirebaseUser): OfflineUser {
        userDao.update(
            RoomUser(
                firebaseUser.uid,
                User.Type.FIREBASE,
                firebaseUser.displayName,
                firebaseUser.email
            )
        )
        return fromUser(userDao.load(firebaseUser.uid, User.Type.FIREBASE)!!)
    }

    override suspend fun getUser(type: User.Type, uid: String): OfflineUser? {
        val user = userDao.load(uid, type) ?: return null
        return fromUser(user)
    }

    override suspend fun getStatistic(user: User.Id, dataset: Id): Statistic? {
        TODO("Not yet implemented")
    }

    override suspend fun putStatistic(statistic: Statistic) {
        TODO("Not yet implemented")
    }
}