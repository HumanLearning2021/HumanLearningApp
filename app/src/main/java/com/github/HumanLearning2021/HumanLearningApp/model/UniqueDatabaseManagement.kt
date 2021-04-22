package com.github.HumanLearning2021.HumanLearningApp.model

import android.content.Context
import com.github.HumanLearning2021.HumanLearningApp.firestore.CachedFirestoreDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.firestore.FirestoreCategorizedPicture
import com.github.HumanLearning2021.HumanLearningApp.firestore.FirestoreDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.firestore.FirestoreDatabaseService
import com.github.HumanLearning2021.HumanLearningApp.offline.*
import com.github.HumanLearning2021.HumanLearningApp.room.*

class UniqueDatabaseManagement(private val context: Context) {

    private val room = RoomOfflineDatabase.getDatabase(context)
    private val databaseDao = room.databaseDao()
    private val datasetDao = room.datasetDao()
    private val categoryDao = room.categoryDao()

    private val downloadedDatabases: MutableList<String> = mutableListOf()

    init {
        val dsNames = room.databaseDao().loadAll().map { db -> db.emptyHLDatabase.databaseName }
        dsNames.forEach { dsName -> downloadedDatabases.add(dsName) }
    }

    fun getDownloadedDatabases(): List<String> = downloadedDatabases.toList()

    suspend fun getDatabases(): List<String> = FirestoreDatabaseService.getDatabaseNames()

    fun accessDatabase(databaseName: String): DatabaseManagement {
        return if (downloadedDatabases.contains(databaseName)) {
            OfflineDatabaseManagement(databaseName).initialize(context)
        } else {
            CachedFirestoreDatabaseManagement(databaseName).initialize(context)
        }
    }

    fun accessCloudDatabase(databaseName: String): CachedFirestoreDatabaseManagement {
        return CachedFirestoreDatabaseManagement(databaseName).initialize(context)
    }

    suspend fun downloadDatabase(databaseName: String): OfflineDatabaseManagement {
        val firestoreDbManagement =
            FirestoreDatabaseManagement(FirestoreDatabaseService(databaseName))
        val pictureRepository = PictureRepository(databaseName, context)

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
                RoomUnlinkedRepresentativePicture(pic.id as String, pictureRepository.savePicture(pic as FirestoreCategorizedPicture), pic.category.id)
            }

        initializeRoomEntities(databaseName, roomDatasets, roomCats, roomPics, roomRepresentativePictures)
        initializeRoomCrossRefs(dbDsRefs, dbCatRefs, dbPicRefs, dsCatRefs)
        downloadedDatabases.add(databaseName)
        return OfflineDatabaseManagement(databaseName).initialize(context)
    }

    fun removeOfflineDatabase(databaseName: String) {
        room.databaseDao().delete(RoomEmptyHLDatabase(databaseName))
        downloadedDatabases.remove(databaseName)
    }

    suspend fun updateFromCloud(databaseName: String) {
        TODO("To be implemented next sprint")
    }

    suspend fun updateTowardsCloud(databaseName: String) {
        TODO("To be implemented next sprint")
    }

    private fun initializeRoomEntities(dbName: String, datasets: List<RoomDatasetWithoutCategories>, categories: List<RoomCategory>, pictures: List<RoomPicture>, representativePictures: List<RoomUnlinkedRepresentativePicture>) {
        databaseDao.insertAll(RoomEmptyHLDatabase(dbName))
        datasetDao.insertAll(*datasets.toTypedArray())
        categoryDao.insertAll(*categories.toTypedArray())
        categoryDao.insertAll(*pictures.toTypedArray())
        categoryDao.insertAll(*representativePictures.toTypedArray())
    }

    private fun initializeRoomCrossRefs(dbDsRefs: List<RoomDatabaseDatasetsCrossRef>, dbCatRefs: List<RoomDatabaseCategoriesCrossRef>, dbPicRefs: List<RoomDatabasePicturesCrossRef>, dsCatRefs: List<RoomDatasetCategoriesCrossRef>) {
        databaseDao.insertAll(*dbDsRefs.toTypedArray())
        databaseDao.insertAll(*dbCatRefs.toTypedArray())
        databaseDao.insertAll(*dbPicRefs.toTypedArray())
        datasetDao.insertAll(*dsCatRefs.toTypedArray())
    }
}
