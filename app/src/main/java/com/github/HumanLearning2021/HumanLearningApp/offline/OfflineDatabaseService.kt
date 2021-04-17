package com.github.HumanLearning2021.HumanLearningApp.offline

import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import com.github.HumanLearning2021.HumanLearningApp.model.*
import com.github.HumanLearning2021.HumanLearningApp.offline.OfflineConverters.fromCategory
import com.github.HumanLearning2021.HumanLearningApp.offline.OfflineConverters.fromPicture
import com.github.HumanLearning2021.HumanLearningApp.room.RoomCategory
import com.github.HumanLearning2021.HumanLearningApp.room.RoomOfflineDatabase
import com.github.HumanLearning2021.HumanLearningApp.room.RoomPicture
import com.google.firebase.auth.FirebaseUser
import java.util.*

object OfflineDatabaseService: DatabaseService {

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
        val randomId = categoryDao.loadAllPictures(category.id as String).pictures.map { p -> p.pictureId }.random()
        return fromPicture(categoryDao.loadPicture(randomId), categoryDao)
    }

    override suspend fun getPicture(pictureId: Any): OfflineCategorizedPicture? {
        require(pictureId is String)
        return fromPicture(categoryDao.loadPicture(pictureId), categoryDao)
    }

    /**
     * A function to retrieve a picture from the database given a category
     *
     * @param category the category of the image to be retrieved
     * @return a CategorizedPicture from the desired category. Null if no picture of the desired
     * category is present in the database or if the category is not present in the database
     */
    override suspend fun getPictureIds(category: Category): List<String> {
        return categoryDao.loadAllPictures(category.id as String).pictures.map { p -> p.pictureId }
    }

    override suspend fun getRepresentativePicture(categoryId: Any): OfflineCategorizedPicture? {
        require(categoryId is String)
        return fromPicture(categoryDao.loadRepresentativePicture(categoryId), categoryDao)
    }

    override suspend fun putPicture(picture: Uri, category: Category): OfflineCategorizedPicture {
        val pic = RoomPicture(getID(), picture, category.id as String)
        categoryDao.insertAll(pic)
        return fromPicture(pic, categoryDao)
    }

    override suspend fun getCategory(categoryId: Any): OfflineCategory? {
        require(categoryId is String)
        return fromCategory(categoryDao.loadById(categoryId))
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
        return categoryDao.loadAllPictures(category.id as String).pictures.map{p -> fromPicture(p, categoryDao)}.toSet()
    }

    override suspend fun removeCategory(category: Category) {
        categoryDao.delete(fromCategory(category))
    }

    override suspend fun removePicture(picture: CategorizedPicture) {
        TODO("Not yet implemented")
    }

    override suspend fun putDataset(name: String, categories: Set<Category>): Dataset {
        val ds = RoomCategory(getID(), name)
        val cats = categories.map {  }
    }

    override suspend fun getDataset(id: Any): Dataset? {
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

    override suspend fun removeCategoryFromDataset(dataset: Dataset, category: Category): Dataset {
        TODO("Not yet implemented")
    }

    override suspend fun editDatasetName(dataset: Dataset, newName: String): Dataset {
        TODO("Not yet implemented")
    }

    override suspend fun addCategoryToDataset(dataset: Dataset, category: Category): Dataset {
        TODO("Not yet implemented")
    }

    override suspend fun updateUser(firebaseUser: FirebaseUser): User {
        TODO("Not yet implemented")
    }

    override suspend fun getUser(type: User.Type, uid: String): User? {
        TODO("Not yet implemented")
    }
}