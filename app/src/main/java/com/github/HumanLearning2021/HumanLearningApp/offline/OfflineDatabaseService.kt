package com.github.HumanLearning2021.HumanLearningApp.offline

import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import com.github.HumanLearning2021.HumanLearningApp.firestore.FirestoreUser
import com.github.HumanLearning2021.HumanLearningApp.model.*
import com.github.HumanLearning2021.HumanLearningApp.offline.OfflineConverters.fromCategory
import com.github.HumanLearning2021.HumanLearningApp.offline.OfflineConverters.fromDataset
import com.github.HumanLearning2021.HumanLearningApp.offline.OfflineConverters.fromPicture
import com.github.HumanLearning2021.HumanLearningApp.offline.OfflineConverters.fromUser
import com.github.HumanLearning2021.HumanLearningApp.room.*
import com.google.firebase.auth.FirebaseUser
import java.lang.IllegalArgumentException
import java.util.*

class OfflineDatabaseService internal constructor(
    val dbName: String
): DatabaseService {

    private val room = RoomOfflineDatabase.getDatabase(ApplicationProvider.getApplicationContext())
    private val datasetDao = room.datasetDao()
    private val categoryDao = room.categoryDao()
    private val userDao = room.userDao()

    private fun getID() = "${UUID.randomUUID()}"

    /**
     * A function to retrieve a picture from the database given a category
     *
     * @param category the category of the image to be retrieved
     * @return a CategorizedPicture from the desired category. Null if no picture of the desired
     * category is present in the database or if the category is not present
     */
    override suspend fun getPicture(category: Category): OfflineCategorizedPicture? {
        val pics = categoryDao.loadAllPictures(category.id as String) ?: return null
        val randomId = pics.pictures.map { p -> p.pictureId }.random()
        return fromPicture(categoryDao.loadPicture(randomId)!!, categoryDao)
    }

    override suspend fun getPicture(pictureId: Any): OfflineCategorizedPicture? {
        require(pictureId is String)
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
        val pics = categoryDao.loadAllPictures(category.id as String) ?: return listOf()
        return pics.pictures.map { p -> p.pictureId }
    }

    override suspend fun getRepresentativePicture(categoryId: Any): OfflineCategorizedPicture? {
        require(categoryId is String)
        val cat = categoryDao.loadRepresentativePicture(categoryId) ?: return null
        return fromPicture(cat, categoryDao)
    }

    override suspend fun putPicture(picture: Uri, category: Category): OfflineCategorizedPicture {
        val cat = categoryDao.loadById(category.id as String) ?: throw IllegalArgumentException("The category with id ${category.id} is not contained in the database")
        val pic = RoomPicture(getID(), picture, cat.categoryId)
        categoryDao.insertAll(pic)
        return fromPicture(pic, categoryDao)
    }

    override suspend fun getCategory(categoryId: Any): OfflineCategory? {
        require(categoryId is String)
        val cat = categoryDao.loadById(categoryId) ?: return null
        return fromCategory(cat)
    }

    override suspend fun putCategory(categoryName: String): OfflineCategory {
        val cat = RoomCategory(getID(), categoryName)
        categoryDao.insertAll(cat)
        return fromCategory(cat)
    }

    override suspend fun getCategories(): Set<OfflineCategory> {
        return categoryDao.loadAll().map{c -> fromCategory(c)}.toSet()
    }

    /**
     * Retrieves all the pictures categorized with the specified category
     *
     * @param category - the category whose pictures we want to retrieve
     * @return the pictures categorized with the specified category, empty if the category is not contained in the dataset
     */
    override suspend fun getAllPictures(category: Category): Set<OfflineCategorizedPicture> {
        val cats = categoryDao.loadAllPictures(category.id as String) ?: return setOf()
        return cats.pictures.map{p -> fromPicture(p, categoryDao)}.toSet()
    }

    /**
     * Remove the category from the database and from all the datasets contained in this database and using this category
     *
     * @param category - the category to remove from the database
     */
    override suspend fun removeCategory(category: Category) {
        categoryDao.delete(fromCategory(category))
        val refs = datasetDao.loadAll(category.id as String)
        datasetDao.delete(*refs.toTypedArray())
    }

    /**
     * Removes the corresponding picture from the database
     *
     * @param picture - the picture to remove from the database
     */
    override suspend fun removePicture(picture: CategorizedPicture) {
        val pic = categoryDao.loadPicture(picture.id as String)
        if (pic != null) {
            categoryDao.delete(pic)
        }
    }

    override suspend fun putDataset(name: String, categories: Set<Category>): OfflineDataset {
        val id = getID()
        val ds = RoomDatasetWithoutCategories(id, name)
        val refs = mutableListOf<RoomDatasetCategoriesCrossRef>()
        for (c in categories) {
            refs.add(RoomDatasetCategoriesCrossRef(id, c.id as String))
        }
        val cats = categories.map{c -> fromCategory(c)}
        datasetDao.insertAll(ds)
        datasetDao.insertAll(*refs.toTypedArray())
        return fromDataset(RoomDataset(ds, cats), categoryDao)
    }

    override suspend fun getDataset(id: Any): OfflineDataset? {
        require(id is String)
        val ds = datasetDao.loadById(id) ?: return null
        return fromDataset(ds, categoryDao)
    }

    /**
     * Deletes the specified dataset from the database
     *
     * @param id - the name of the dataset to delete
     */
    override suspend fun deleteDataset(id: Any) {
        require(id is String)
        val ds = datasetDao.loadById(id)
        if (ds != null) {
            val refs = datasetDao.loadAll(ds.datasetWithoutCategories.datasetId)
            datasetDao.delete(*refs.toTypedArray())
            datasetDao.delete(ds.datasetWithoutCategories)
        }
    }

    override suspend fun putRepresentativePicture(picture: Uri, category: Category) {
        val cat = categoryDao.loadById(category.id as String) ?: throw IllegalArgumentException("The category with id ${category.id} is not contained in the database")
        categoryDao.insertAll(RoomUnlinkedRepresentativePicture(getID(), picture, cat.categoryId))
    }

    override suspend fun getDatasets(): Set<Dataset> {
        return datasetDao.loadAll().map { d -> fromDataset(d, categoryDao) }.toSet()
    }

    override suspend fun removeCategoryFromDataset(dataset: Dataset, category: Category): OfflineDataset {
        val ref = datasetDao.delete(RoomDatasetCategoriesCrossRef(dataset.id as String, category.id as String))
        val ds = datasetDao.loadById(dataset.id as String) ?: throw IllegalArgumentException("The dataset with id ${dataset.id} is not contained in the database")
        return fromDataset(ds, categoryDao)
    }

    override suspend fun editDatasetName(dataset: Dataset, newName: String): OfflineDataset {
        val ds = datasetDao.loadById(dataset.id as String) ?: throw IllegalArgumentException("The dataset with id ${dataset.id} is not contained in the database")
        val updatedDs = RoomDatasetWithoutCategories(ds.datasetWithoutCategories.datasetId, newName)
        datasetDao.update(updatedDs)
        return fromDataset(RoomDataset(updatedDs, ds.categories), categoryDao)
    }

    override suspend fun addCategoryToDataset(dataset: Dataset, category: Category): OfflineDataset {
        val ds = datasetDao.loadById(dataset.id as String) ?: throw IllegalArgumentException("The dataset with id ${dataset.id} is not contained in the database")
        val cat = categoryDao.loadById(category.id as String) ?: throw IllegalArgumentException("The category with id ${category.id} is not contained in the database")
        datasetDao.insertAll(RoomDatasetCategoriesCrossRef(ds.datasetWithoutCategories.datasetId, cat.categoryId))
        val updatedCats = ds.categories.toMutableList()
        updatedCats.add(cat)
        return fromDataset(RoomDataset(ds.datasetWithoutCategories, updatedCats), categoryDao)
    }

    override suspend fun updateUser(firebaseUser: FirebaseUser): OfflineUser {
        userDao.update(RoomUser(firebaseUser.uid, User.Type.FIREBASE, firebaseUser.displayName, firebaseUser.email))
        return fromUser(userDao.load(firebaseUser.uid, User.Type.FIREBASE)!!)
    }

    override suspend fun getUser(type: User.Type, uid: String): OfflineUser? {
        val user = userDao.load(uid, type) ?: return null
        return fromUser(user)
    }
}