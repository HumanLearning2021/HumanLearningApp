package com.github.HumanLearning2021.HumanLearningApp.model

import android.content.Context
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import com.github.HumanLearning2021.HumanLearningApp.firestore.CachedFirestoreDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.firestore.FirestoreDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.firestore.FirestoreDatabaseService
import com.github.HumanLearning2021.HumanLearningApp.offline.*
import com.github.HumanLearning2021.HumanLearningApp.offline.OfflineConverters.fromPicture
import com.github.HumanLearning2021.HumanLearningApp.room.RoomOfflineDatabase

class UniqueDatabaseManagement {

    private val context: Context = ApplicationProvider.getApplicationContext()
    private val room = RoomOfflineDatabase.getDatabase(context)
    private val databaseDao = room.databaseDao()
    private val datasetDao = room.datasetDao()
    private val categoryDao = room.categoryDao()
    private val userDao = room.userDao()

    private val downloadedDatabases: MutableList<String> = mutableListOf()

    init {
        val dsNames = databaseDao.loadAll().map { db -> db.emptyHLDatabase.databaseName }
        dsNames.forEach { dsName -> downloadedDatabases.add(dsName) }
    }

    suspend fun accessDatabase(databaseName: String): DatabaseManagement {
        return if (downloadedDatabases.contains(databaseName)) {
            OfflineDatabaseManagement(databaseName)
        } else {
            CachedFirestoreDatabaseManagement(databaseName)
        }
    }

    suspend fun accessDatabaseFromCloud(databaseName: String): DatabaseManagement {
        return CachedFirestoreDatabaseManagement(databaseName)
    }

    suspend fun downloadDatabase(databaseName: String) {

    }

    suspend fun updateFromCloud(databaseName: String) {
        TODO("To be implemented next sprint")
    }

    suspend fun updateTowardsCloud(databaseName: String) {
        TODO("To be implemented next sprint")
    }
}