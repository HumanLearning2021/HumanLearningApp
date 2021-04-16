package com.github.HumanLearning2021.HumanLearningApp.offline

import android.net.Uri
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.github.HumanLearning2021.HumanLearningApp.model.*
import com.github.HumanLearning2021.HumanLearningApp.room.RoomOfflineDatabase
import com.google.firebase.auth.FirebaseUser

class OfflineDatabaseService: DatabaseService {

    private val room = RoomOfflineDatabase.getDatabase(ApplicationProvider.getApplicationContext())
    private val datasetDao = room.datasetDao()
    private val categoryDao = room.categoryDao()
    private val userDao = room.userDao()

    override suspend fun getPicture(category: Category): CategorizedPicture? {
        TODO("Not yet implemented")
    }

    override suspend fun getRepresentativePicture(categoryId: Any): CategorizedPicture? {
        TODO("Not yet implemented")
    }

    override suspend fun putPicture(picture: Uri, category: Category): CategorizedPicture {
        TODO("Not yet implemented")
    }

    override suspend fun getCategory(categoryId: Any): Category? {
        TODO("Not yet implemented")
    }

    override suspend fun putCategory(categoryName: String): Category {
        TODO("Not yet implemented")
    }

    override suspend fun getCategories(): Set<Category> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllPictures(category: Category): Set<CategorizedPicture> {
        TODO("Not yet implemented")
    }

    override suspend fun removeCategory(category: Category) {
        TODO("Not yet implemented")
    }

    override suspend fun removePicture(picture: CategorizedPicture) {
        TODO("Not yet implemented")
    }

    override suspend fun putDataset(name: String, categories: Set<Category>): Dataset {
        TODO("Not yet implemented")
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