package com.github.HumanLearning2021.HumanLearningApp.model

import android.content.Context
import com.github.HumanLearning2021.HumanLearningApp.firestore.FirestoreDatabaseService
import com.github.HumanLearning2021.HumanLearningApp.hilt.DummyDatabase
import com.github.HumanLearning2021.HumanLearningApp.offline.CachePictureRepository
import com.github.HumanLearning2021.HumanLearningApp.offline.CachedDatabaseService
import com.github.HumanLearning2021.HumanLearningApp.offline.OfflineDatabaseService
import com.github.HumanLearning2021.HumanLearningApp.offline.PictureRepository
import com.github.HumanLearning2021.HumanLearningApp.room.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import javax.inject.Inject

/**
 * @param context: the application context
 */
class UniqueDatabaseManagement @Inject constructor(
    val context: Context,
    private val room: RoomOfflineDatabase,
    private val firestore: FirebaseFirestore,
    @DummyDatabase var dummyDb: DatabaseManagement
) {


    private val databaseDao = room.databaseDao()
    private val datasetDao = room.datasetDao()
    private val categoryDao = room.categoryDao()

    fun getDownloadedDatabases(): List<String> = runBlocking {
        room.databaseDao().loadAll().map { db -> db.emptyHLDatabase.databaseName }.toList()
    }

    fun getDatabases(): List<String> = runBlocking {
        FirestoreDatabaseService.getDatabaseNames()
    }

    fun accessDatabase(databaseName: String): DefaultDatabaseManagement = runBlocking {
        when {
            // necessary for current testing setup
            databaseName == "dummy" -> {
                dummyDb as DefaultDatabaseManagement
            }
            room.databaseDao().loadAll().map { db -> db.emptyHLDatabase.databaseName }
                .contains(databaseName) -> {
                DefaultDatabaseManagement(OfflineDatabaseService(databaseName, context, room))
            }
            else -> {
                DefaultDatabaseManagement(
                    CachedDatabaseService(
                        FirestoreDatabaseService(
                            databaseName,
                            firestore
                        ), CachePictureRepository(databaseName, context)
                    )
                )
            }
        }
    }

    fun accessCloudDatabase(databaseName: String): DatabaseManagement {
        return DefaultDatabaseManagement(
            CachedDatabaseService(
                FirestoreDatabaseService(
                    databaseName,
                    firestore
                ), CachePictureRepository(databaseName, context)
            )
        )
    }

    fun downloadDatabase(databaseName: String): Deferred<DefaultDatabaseManagement> =
        CoroutineScope(Dispatchers.IO).async {
            CachePictureRepository(
                databaseName,
                context
            ).clear() //TODO("reuse content from cache instead")
            val firestoreDbManagement =
                // necessary for current testing setup
                if (databaseName == "dummy") {
                    DefaultDatabaseManagement(DummyDatabaseService())
                } else {
                    DefaultDatabaseManagement(
                        FirestoreDatabaseService(
                            databaseName,
                            firestore
                        )
                    )
                }
            val pictureRepository = PictureRepository(databaseName, context)

            val datasets = firestoreDbManagement.getDatasets()
            val categories = datasets.flatMap { ds -> ds.categories }
            val pictures =
                withContext(Dispatchers.IO) {
                    categories.map { cat ->
                        firestoreDbManagement.getAllPictures(
                            cat
                        )
                    }.flatten()
                }

            val dbPicRefs =
                pictures.map { pic -> RoomDatabasePicturesCrossRef(databaseName, pic.id) }
            val dbDsRefs = datasets.map { ds -> RoomDatabaseDatasetsCrossRef(databaseName, ds.id) }
            val dbCatRefs =
                categories.map { cat -> RoomDatabaseCategoriesCrossRef(databaseName, cat.id) }
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
                RoomUnlinkedRepresentativePicture(
                    pic.id,
                    pictureRepository.savePicture(pic),
                    pic.category.id
                )
            }

            initializeRoomEntities(
                databaseName,
                roomDatasets,
                roomCats,
                roomPics,
                roomRepresentativePictures
            )
            initializeRoomCrossRefs(dbDsRefs, dbCatRefs, dbPicRefs, dsCatRefs)
            DefaultDatabaseManagement(OfflineDatabaseService(databaseName, context, room))
        }

    fun removeDatabaseFromDownloadsAsync(databaseName: String) =
        CoroutineScope(Dispatchers.IO).async {
            room.databaseDao().delete(RoomEmptyHLDatabase(databaseName))
            OfflineDatabaseService(databaseName, context, room).clear()
        }

    private suspend fun initializeRoomEntities(
        dbName: String,
        datasets: List<RoomDatasetWithoutCategories>,
        categories: List<RoomCategory>,
        pictures: List<RoomPicture>,
        representativePictures: List<RoomUnlinkedRepresentativePicture>
    ) {
        databaseDao.insertAll(RoomEmptyHLDatabase(dbName))
        datasetDao.insertAll(*datasets.toTypedArray())
        categoryDao.insertAll(*categories.toTypedArray())
        categoryDao.insertAll(*pictures.toTypedArray())
        categoryDao.insertAll(*representativePictures.toTypedArray())
    }

    private suspend fun initializeRoomCrossRefs(
        dbDsRefs: List<RoomDatabaseDatasetsCrossRef>,
        dbCatRefs: List<RoomDatabaseCategoriesCrossRef>,
        dbPicRefs: List<RoomDatabasePicturesCrossRef>,
        dsCatRefs: List<RoomDatasetCategoriesCrossRef>
    ) {
        databaseDao.insertAll(*dbDsRefs.toTypedArray())
        databaseDao.insertAll(*dbCatRefs.toTypedArray())
        databaseDao.insertAll(*dbPicRefs.toTypedArray())
        datasetDao.insertAll(*dsCatRefs.toTypedArray())
    }
}
