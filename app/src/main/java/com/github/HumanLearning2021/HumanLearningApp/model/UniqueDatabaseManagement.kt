package com.github.HumanLearning2021.HumanLearningApp.model

import android.content.Context
import com.github.HumanLearning2021.HumanLearningApp.firestore.FirestoreDatabaseService
import com.github.HumanLearning2021.HumanLearningApp.hilt.DummyDatabase
import com.github.HumanLearning2021.HumanLearningApp.hilt.RoomDatabase
import com.github.HumanLearning2021.HumanLearningApp.offline.CachedDatabaseService
import com.github.HumanLearning2021.HumanLearningApp.offline.OfflineDatabaseService
import com.github.HumanLearning2021.HumanLearningApp.offline.PictureCache
import com.github.HumanLearning2021.HumanLearningApp.room.*
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import javax.inject.Inject

/**
 * The application's entry point to access any database
 * @property context the application context
 * @property room the Room database used for on device storage
 * @property firestore Firebase Firestore used for cloud storage
 * @property dummyDb dummy database (required for testing purposes and has to be injected for it to
 * be a singleton)
 */
class UniqueDatabaseManagement @Inject constructor(
    @ApplicationContext val context: Context,
    @RoomDatabase val room: RoomOfflineDatabase,
    private val firestore: FirebaseFirestore,
    @DummyDatabase val dummyDb: DatabaseManagement
) {

    private val databaseDao = room.databaseDao()
    private val datasetDao = room.datasetDao()
    private val categoryDao = room.categoryDao()

    /**
     * Gets the names of all the currently downloaded databases
     * @return list containing the names of all the currently downloaded databases
     */
    fun getDownloadedDatabases(): List<String> = runBlocking {
        room.databaseDao().loadAll().map { db -> db.emptyHLDatabase.databaseName }.toList()
    }

    /**
     * Gets the names of all the databases available on cloud storage
     * @return list containing the names of all the databases available on cloud storage
     */
    fun getCloudDatabases(): List<String> = runBlocking {
        FirestoreDatabaseService.getDatabaseNames()
    }

    /**
     * Function to get the entry point to a desired database
     * @param databaseName of the desired database
     * @return the database management used to access the database. The underlying database will
     * be either the cloud or offline version, depending on if it is currently downloaded or not
     */
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
                        ), PictureCache.applicationPictureCache(databaseName, context)
                    )
                )
            }
        }
    }

    /**
     * Function to get the entry point to a desired database on the cloud storage.
     * @param databaseName of the desired database
     * @return the database management used to access the database
     */
    fun accessCloudDatabase(databaseName: String): DatabaseManagement {
        return DefaultDatabaseManagement(
            CachedDatabaseService(
                FirestoreDatabaseService(
                    databaseName,
                    firestore
                ), PictureCache.applicationPictureCache(databaseName, context)
            )
        )
    }

    /**
     * Downloads the desired database from cloud storage to on device storage.
     * @param databaseName of the desired database
     * @return the database management used to access the downloaded database
     */
    suspend fun downloadDatabase(databaseName: String): DatabaseManagement =
        withContext(Dispatchers.IO) {
            PictureCache.applicationPictureCache(databaseName, context)
                .clear() //TODO("reuse content from cache instead")
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
            val pictureRepository = PictureCache(databaseName, context)

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
    
    /**
     * Removes a database from on device storage asynchronously.
     * @param databaseName of the database to remove from on device storage
     * @return a job tracking completion of the operation
     */
    fun removeDatabaseFromDownloads(databaseName: String): Job =
        CoroutineScope(Dispatchers.IO).launch {
            databaseDao.loadByName(databaseName)?.let {
                it.datasets.forEach { ds -> datasetDao.delete(ds) }
                it.categories.forEach { cat -> categoryDao.delete(cat) }
            }
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
