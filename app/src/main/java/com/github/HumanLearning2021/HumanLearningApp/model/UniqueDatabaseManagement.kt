package com.github.HumanLearning2021.HumanLearningApp.model

import android.content.Context
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import com.github.HumanLearning2021.HumanLearningApp.firestore.FirestoreDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.firestore.FirestoreDatabaseService
import com.github.HumanLearning2021.HumanLearningApp.offline.*
import com.github.HumanLearning2021.HumanLearningApp.offline.OfflineConverters.fromPicture
import com.github.HumanLearning2021.HumanLearningApp.room.RoomOfflineDatabase

class UniqueDatabaseManagement internal constructor(
    private val dbName: String
) {

    private val context: Context = ApplicationProvider.getApplicationContext()
    private val room = RoomOfflineDatabase.getDatabase(context)
    private val databaseDao = room.databaseDao()
    private val datasetDao = room.datasetDao()
    private val categoryDao = room.categoryDao()
    private val userDao = room.userDao()

    private val picRepository: PictureRepository

    private val downloadedDatasets: MutableList<String> = mutableListOf()

    init {
        picRepository = PictureRepository(dbName, context)
        val dsNames = databaseDao.loadByName(dbName)!!.datasets.map { ds -> ds.datasetId }
        dsNames.forEach { dsName -> downloadedDatasets.add(dsName) }
    }

    suspend fun downloadDataset(id: Any) {

    }

    suspend fun downloadDatabase() {

    }

    suspend fun updateFromCloud() {

    }

    suspend fun updateTowardsCloud() {

    }
}