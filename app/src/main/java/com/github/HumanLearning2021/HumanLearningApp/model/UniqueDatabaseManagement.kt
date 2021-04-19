package com.github.HumanLearning2021.HumanLearningApp.model

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.github.HumanLearning2021.HumanLearningApp.firestore.CachedFirestoreDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.firestore.FirestoreDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.firestore.FirestoreDatabaseService
import com.github.HumanLearning2021.HumanLearningApp.model.Converters.fromPicture
import com.github.HumanLearning2021.HumanLearningApp.offline.*
import com.github.HumanLearning2021.HumanLearningApp.room.*

class UniqueDatabaseManagement {

    private val context: Context = ApplicationProvider.getApplicationContext()
    private val room = RoomOfflineDatabase.getDatabase(context)

    private val downloadedDatabases: MutableList<String> = mutableListOf()

    init {
        val dsNames = room.databaseDao().loadAll().map { db -> db.emptyHLDatabase.databaseName }
        dsNames.forEach { dsName -> downloadedDatabases.add(dsName) }
    }

    fun accessDatabase(databaseName: String): DatabaseManagement {
        return if (downloadedDatabases.contains(databaseName)) {
            OfflineDatabaseManagement(databaseName)
        } else {
            CachedFirestoreDatabaseManagement(databaseName)
        }
    }

    fun accessDatabaseFromCloud(databaseName: String): DatabaseManagement {
        return CachedFirestoreDatabaseManagement(databaseName)
    }

    suspend fun downloadDatabase(databaseName: String): DatabaseManagement {
        val firestoreDbManagement =
            FirestoreDatabaseManagement(FirestoreDatabaseService(databaseName))
        val pictureRepository = PictureRepository(databaseName, context)
        val databaseDao = room.databaseDao()
        val datasetDao = room.datasetDao()
        val categoryDao = room.categoryDao()

        val datasets = firestoreDbManagement.getDatasets()
        val categories = firestoreDbManagement.getCategories()
        val pictures = categories.map { cat -> firestoreDbManagement.getAllPictures(cat) }.flatten()
        val dbPicRefs = pictures.map { pic -> RoomDatabasePicturesCrossRef(databaseName, pic.id) }
        val dbDsRefs = datasets.map { ds -> RoomDatabaseDatasetsCrossRef(databaseName, ds.id) }
        val dbCatRefs = categories.map { cat -> RoomDatabaseCategoriesCrossRef(databaseName, cat.id) }
        val dsCatRefs = datasets.map { ds -> ds.id to ds.categories }.flatMap { (dsId, cats) ->
                cats.map { cat -> RoomDatasetCategoriesCrossRef(dsId, cat.id) }
            }
        val roomDatasets = datasets.map { ds -> RoomDatasetWithoutCategories(ds.id, ds.name) }
        val roomCats = categories.map { cat -> RoomCategory(cat.id, cat.name) }
        val roomPics = pictures.map { pic ->
                RoomPicture(pic.id, pictureRepository.savePicture(pic), pic.category.id)
            }
        val roomRepresentativePictures = categories.mapNotNull { cat ->
             firestoreDbManagement.getRepresentativePicture(cat.id)
            }.map { pic ->
                RoomUnlinkedRepresentativePicture(pic.id, pictureRepository.savePicture(pic), pic.category.id)
            }

        databaseDao.insertAll(RoomEmptyHLDatabase(databaseName))
        databaseDao.insertAll(*dbDsRefs.toTypedArray())
        databaseDao.insertAll(*dbCatRefs.toTypedArray())
        databaseDao.insertAll(*dbPicRefs.toTypedArray())
        datasetDao.insertAll(*dsCatRefs.toTypedArray())
        datasetDao.insertAll(*roomDatasets.toTypedArray())
        categoryDao.insertAll(*roomCats.toTypedArray())
        categoryDao.insertAll(*roomPics.toTypedArray())
        categoryDao.insertAll(*roomRepresentativePictures.toTypedArray())

        downloadedDatabases.add(databaseName)
        return OfflineDatabaseManagement(databaseName)
    }

    suspend fun removeOfflineDatabase(databaseName: String) {
        //TODO("uncomment once implemented")
        //updateTowardsCloud(databaseName)
        room.databaseDao().delete(RoomEmptyHLDatabase(databaseName))
        downloadedDatabases.remove(databaseName)
    }

    suspend fun updateFromCloud(databaseName: String) {
        TODO("To be implemented next sprint")
    }

    suspend fun updateTowardsCloud(databaseName: String) {
        TODO("To be implemented next sprint")
    }
}