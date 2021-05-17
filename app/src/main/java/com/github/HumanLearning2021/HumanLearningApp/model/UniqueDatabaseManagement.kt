package com.github.HumanLearning2021.HumanLearningApp.model

import android.content.Context
import android.util.Log
import com.github.HumanLearning2021.HumanLearningApp.firestore.FirestoreCategorizedPicture
import com.github.HumanLearning2021.HumanLearningApp.firestore.FirestoreCategory
import com.github.HumanLearning2021.HumanLearningApp.firestore.FirestoreDatabaseService
import com.github.HumanLearning2021.HumanLearningApp.firestore.FirestoreDataset
import com.github.HumanLearning2021.HumanLearningApp.offline.CachePictureRepository
import com.github.HumanLearning2021.HumanLearningApp.offline.CachedDatabaseService
import com.github.HumanLearning2021.HumanLearningApp.offline.OfflineDatabaseService
import com.github.HumanLearning2021.HumanLearningApp.offline.PictureRepository
import com.github.HumanLearning2021.HumanLearningApp.room.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @param context: the application context
 */
class UniqueDatabaseManagement constructor(
    val context: Context,
    private val room: RoomOfflineDatabase,
    private val firestore: FirebaseFirestore
) {

    private val databaseDao = room.databaseDao()
    private val datasetDao = room.datasetDao()
    private val categoryDao = room.categoryDao()

    suspend fun getDownloadedDatabases(): List<String> =
        withContext(Dispatchers.IO) {
            room.databaseDao().loadAll().map { db -> db.emptyHLDatabase.databaseName }.toList()
        }

    suspend fun getDatabases(): List<String> =
        withContext(Dispatchers.IO) { FirestoreDatabaseService.getDatabaseNames() }

    suspend fun accessDatabase(databaseName: String): DatabaseManagement =
        withContext(Dispatchers.IO) {
            if (room.databaseDao().loadAll().map { db -> db.emptyHLDatabase.databaseName }
                    .contains(databaseName)) {
                DefaultDatabaseManagement(OfflineDatabaseService(databaseName, context, room))
            } else {
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

    suspend fun downloadDatabase(databaseName: String): DatabaseManagement =
        withContext(Dispatchers.IO) {
            CachePictureRepository(
                databaseName,
                context
            ).clear() //TODO("reuse content from cache instead")
            Log.d("db download debugging", "cache cleared")
            val firestoreDbManagement =
                DefaultDatabaseManagement(
                    FirestoreDatabaseService(
                        databaseName,
                        firestore
                    )
                )
            Log.d("db download debugging", "firestore db management set up")
            val pictureRepository = PictureRepository(databaseName, context)
            Log.d("db download debugging", "picture repository set up")

            //val datasets = firestoreDbManagement.getDatasets()
            val datasets = setOf(
                FirestoreDataset(
                    "RzvGIaniVkBGHQsQvpza", "IT stuff", setOf(
                        FirestoreCategory(
                            "1F9Qvw8r17RciWgHYdDD",
                            "laptop"
                        ), FirestoreCategory(
                            "6x0Ul3s0WsPiZZyA1ihc", "headphones"
                        ), FirestoreCategory(
                            "mj7ODhHLjoJWSiKcUfuW", "mouse"
                        )
                    )
                )
            )
            Log.d("db download debugging", "firestore datasets loaded")
            val categories = datasets.flatMap { ds -> ds.categories }
            Log.d("db download debugging", "firestore categories loaded")
//            val pictures =
//                withContext(Dispatchers.IO) {
//                    categories.map { cat ->
//                        firestoreDbManagement.getAllPictures(
//                            cat
//                        )
//                    }.flatten()
//                }
            val pictures = setOf(
                FirestoreCategorizedPicture(
                    "AOK5KI9YfkSm2KgOqTW6",
                    FirestoreCategory(
                        "mj7ODhHLjoJWSiKcUfuW", "mouse"
                    ),
                    "gs://human-learning-app.appspot.com//demo2/images/c2b096ae-05b7-46dc-acb4-6845c90e532d"
                ), FirestoreCategorizedPicture(
                    "2BcorNexsjnchovcNsk6",
                    FirestoreCategory(
                        "mj7ODhHLjoJWSiKcUfuW", "mouse"
                    ),
                    "gs://human-learning-app.appspot.com//demo2/images/a88c6fc8-492f-4ec9-94b2-f8cba3ecd9d8"
                )
            )
            Log.d("db download debugging", "firestore pictures loaded")

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
            Log.d("db download debugging", "room variables set up")
            val roomRepresentativePictures = categories.mapNotNull { cat ->
                firestoreDbManagement.getRepresentativePicture(cat.id)
            }.map { pic ->
                RoomUnlinkedRepresentativePicture(
                    pic.id,
                    pictureRepository.savePicture(pic),
                    pic.category.id
                )
            }
            Log.d("db download debugging", "images downloaded")

            initializeRoomEntities(
                databaseName,
                roomDatasets,
                roomCats,
                roomPics,
                roomRepresentativePictures
            )
            Log.d("db download debugging", "room entities initialized")
            initializeRoomCrossRefs(dbDsRefs, dbCatRefs, dbPicRefs, dsCatRefs)
            Log.d("db download debugging", "room cross refs initialized")
            DefaultDatabaseManagement(OfflineDatabaseService(databaseName, context, room))
        }

    suspend fun removeDatabaseFromDownloads(databaseName: String) =
        withContext(Dispatchers.IO) {
            room.databaseDao().delete(RoomEmptyHLDatabase(databaseName))
            OfflineDatabaseService(databaseName, context, room).clear()
        }

    private suspend fun initializeRoomEntities(
        dbName: String,
        datasets: List<RoomDatasetWithoutCategories>,
        categories: List<RoomCategory>,
        pictures: List<RoomPicture>,
        representativePictures: List<RoomUnlinkedRepresentativePicture>
    ) = withContext(Dispatchers.IO) {
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
    ) = withContext(Dispatchers.IO) {
        databaseDao.insertAll(*dbDsRefs.toTypedArray())
        databaseDao.insertAll(*dbCatRefs.toTypedArray())
        databaseDao.insertAll(*dbPicRefs.toTypedArray())
        datasetDao.insertAll(*dsCatRefs.toTypedArray())
    }
}
