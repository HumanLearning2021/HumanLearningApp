package com.github.HumanLearning2021.HumanLearningApp.model

import android.content.Context
import android.net.Uri
import com.github.HumanLearning2021.HumanLearningApp.firestore.FirestoreDatabaseService
import com.github.HumanLearning2021.HumanLearningApp.offline.OfflineDatabaseService
import com.github.HumanLearning2021.HumanLearningApp.offline.PictureRepository
import com.github.HumanLearning2021.HumanLearningApp.room.*
import com.google.firebase.firestore.FirebaseFirestore

@Deprecated(
    "replaced by DefaultDatabaseManagement",
    ReplaceWith(
        "DefaultDatabaseManagement",
        "com.github.HumanLearning2021.HumanLearningApp.model.DefaultDatabaseManagement"
    )
)
typealias DummyDatabaseManagement = DefaultDatabaseManagement

class DefaultDatabaseManagement internal constructor(
    private val databaseService: DatabaseService,
    private val databaseName: String,
    private val context: Context,
    private val room: RoomOfflineDatabase,
) : DatabaseManagement {

    private val offlineDatabaseService: DatabaseService =
        OfflineDatabaseService(databaseName, context, room)
    private val databaseDao: DatabaseDao = room.databaseDao()
    private val datasetDao: DatasetDao = room.datasetDao()
    private val categoryDao: CategoryDao = room.categoryDao()

    private val downloadedDatasets: MutableSet<Id> = mutableSetOf()
    private val downloadedCategories: MutableSet<Id> = mutableSetOf()
    private val downloadedPictures: MutableSet<Id> = mutableSetOf()
    private val downloadedReprPictures: MutableSet<Id> = mutableSetOf()
    private var uninitialized = true

    /**
     * Downloads an entire dataset with all it's related elements
     *
     * @param id: id of the dataset to download
     * @throws DatabaseService.NotFoundException if no dataset with this id was found
     */
    suspend fun downloadDataset(id: Id) {
        val pictureRepository = PictureRepository("databaseName$id", context)

        val dataset =
            this.databaseService.getDataset(id) ?: throw DatabaseService.NotFoundException(id)
        val categories = dataset.categories
        val pictures =
            categories.map { cat -> this.databaseService.getAllPictures(cat) }.flatten()
        val dbPicRefs = pictures.map { pic -> RoomDatabasePicturesCrossRef(databaseName, pic.id) }
        val dbDsRef = RoomDatabaseDatasetsCrossRef(databaseName, id)
        val dbCatRefs =
            categories.map { cat -> RoomDatabaseCategoriesCrossRef(databaseName, cat.id) }
        val dsCatRefs = categories.map { cat -> RoomDatasetCategoriesCrossRef(id, cat.id) }
        val roomDataset = RoomDatasetWithoutCategories(id, dataset.name)
        val roomCats = categories.map { cat -> RoomCategory(cat.id, cat.name) }
        val roomPics = pictures.map { pic ->
            RoomPicture(pic.id, pictureRepository.savePicture(pic), pic.category.id)
        }
        val roomRepresentativePictures =
            categories.map { cat -> this.databaseService.getRepresentativePicture(cat.id) }
                .mapNotNull { pic ->
                    pic?.let {
                        RoomUnlinkedRepresentativePicture(
                            it.id,
                            pictureRepository.savePicture(it),
                            it.category.id
                        )
                    }
                }

        initializeRoomEntities(
            databaseName,
            listOf(roomDataset),
            roomCats,
            roomPics,
            roomRepresentativePictures
        )
        initializeRoomCrossRefs(listOf(dbDsRef), dbCatRefs, dbPicRefs, dsCatRefs)
        downloadedDatasets.add(id)
    }

    /**
     * Deletes all the files associated to a downloaded dataset
     *
     * @param id: id of the dataset to remove
     * @throws DatabaseService.NotFoundException if no dataset with this id was found in the downloaded datasets
     */
    fun removeDownloadedDataset(id: Id) {
        val dataset = room.datasetDao().loadById(id) ?: throw DatabaseService.NotFoundException(id)
        val categories = dataset.categories
        val pictures = categories.mapNotNull { cat ->
            room.categoryDao().loadAllPictures(cat.categoryId)?.pictures
        }.flatten()
        val representativePictures = categories.mapNotNull { cat ->
            room.categoryDao().loadRepresentativePicture(cat.categoryId)
        }
        room.datasetDao().delete(dataset.datasetWithoutCategories)
        downloadedDatasets.remove(dataset.datasetWithoutCategories.datasetId)
        downloadedCategories.removeAll(categories.map { cat -> cat.categoryId })
        downloadedPictures.removeAll(pictures.map { pic -> pic.pictureId })
        downloadedReprPictures.removeAll(representativePictures.map { pic -> pic.picture.pictureId })
        PictureRepository("databaseName$id", context).clear()
    }

    override suspend fun getPicture(category: Category): CategorizedPicture? {
        initialize()
        return try {
            if (downloadedCategories.contains(category.id)) {
                offlineDatabaseService.getPicture(category)
            } else {
                this.databaseService.getPicture(category)
            }
        } catch (e: DatabaseService.NotFoundException) {
            throw e
        }
    }

    override suspend fun getPicture(pictureId: Id): CategorizedPicture? {
        initialize()
        return if (downloadedPictures.contains(pictureId)) {
            offlineDatabaseService.getPicture(pictureId)
        } else {
            this.databaseService.getPicture(pictureId)
        }
    }

    override suspend fun getPictureIds(category: Category): List<Id> {
        initialize()
        return try {
            if (downloadedCategories.contains(category.id)) {
                offlineDatabaseService.getPictureIds(category)
            } else {
                this.databaseService.getPictureIds(category)
            }
        } catch (e: DatabaseService.NotFoundException) {
            throw e
        }
    }

    override suspend fun getRepresentativePicture(categoryId: Id): CategorizedPicture? {
        initialize()
        return if (downloadedCategories.contains(categoryId)) {
            offlineDatabaseService.getRepresentativePicture(categoryId)
        } else {
            this.databaseService.getRepresentativePicture(categoryId)
        }
    }

    override suspend fun putPicture(picture: Uri, category: Category): CategorizedPicture {
        initialize()
        return try {
            if (downloadedCategories.contains(category.id)) {
                throw IllegalStateException("category ${category.id} is downloaded and hence it's dataset is read only")
            } else {
                this.databaseService.putPicture(picture, category)
            }
        } catch (e: DatabaseService.NotFoundException) {
            throw e
        }
    }

    override suspend fun getCategoryById(categoryId: Id): Category? {
        initialize()
        return if (downloadedCategories.contains(categoryId)) {
            offlineDatabaseService.getCategory(categoryId)
        } else {
            this.databaseService.getCategory(categoryId)
        }
    }

    override suspend fun getCategoryByName(categoryName: String): Collection<Category> {
        initialize()
        val offlineCategories =
            offlineDatabaseService.getCategories().filter { cat -> cat.name == categoryName }
        val firestoreCategories = this.databaseService.getCategories()
            .filter { cat -> cat.name == categoryName && !downloadedCategories.contains(cat.id) }
        return offlineCategories.union(firestoreCategories)
    }

    override suspend fun putCategory(categoryName: String): Category {
        initialize()
        return this.databaseService.putCategory(categoryName)
    }

    override suspend fun getCategories(): Set<Category> {
        initialize()
        val offlineCategories = offlineDatabaseService.getCategories()
        val firestoreCategories = this.databaseService.getCategories()
            .filter { cat -> !downloadedCategories.contains(cat.id) }
        return offlineCategories.union(firestoreCategories)
    }

    override suspend fun getAllPictures(category: Category): Set<CategorizedPicture> {
        initialize()
        return try {
            if (downloadedCategories.contains(category.id)) {
                offlineDatabaseService.getAllPictures(category)
            } else {
                this.databaseService.getAllPictures(category)
            }
        } catch (e: DatabaseService.NotFoundException) {
            throw e
        }
    }

    override suspend fun removeCategory(category: Category) {
        initialize()
        try {
            if (downloadedCategories.contains(category.id)) {
                throw IllegalStateException("category ${category.id} is downloaded and hence it's dataset is read only")
            } else {
                this.databaseService.removeCategory(category)
            }
        } catch (e: DatabaseService.NotFoundException) {
            //do nothing since this means that the category is not in the database which is the same as having it removed
        }
    }

    override suspend fun removePicture(picture: CategorizedPicture) {
        initialize()
        try {
            if (downloadedPictures.contains(picture.id)) {
                throw IllegalStateException("picture ${picture.id} is downloaded and hence it's dataset is read only")
            } else {
                this.databaseService.removePicture(picture)
            }
        } catch (e: DatabaseService.NotFoundException) {
            //do nothing since this means that the picture is not in the database which is the same as having it removed
        }
    }

    override suspend fun putDataset(name: String, categories: Set<Category>): Dataset {
        initialize()
        return this.databaseService.putDataset(name, categories)
    }

    override suspend fun getDatasetById(id: Id): Dataset? {
        initialize()
        return if (downloadedDatasets.contains(id)) {
            offlineDatabaseService.getDataset(id)
        } else {
            this.databaseService.getDataset(id)
        }
    }

    override suspend fun getDatasetByName(datasetName: String): Collection<Dataset> {
        initialize()
        val offlineDatasets =
            offlineDatabaseService.getDatasets().filter { ds -> ds.name == datasetName }
        val firestoreDatasets = this.databaseService.getDatasets()
            .filter { ds -> ds.name == datasetName && !downloadedDatasets.contains(ds.id) }
        return offlineDatasets.union(firestoreDatasets)
    }

    override suspend fun deleteDataset(id: Id) {
        initialize()
        if (downloadedDatasets.contains(id)) throw IllegalStateException("dataset $id is downloaded and hence read only")
        try {
            this.databaseService.deleteDataset(id)
        } catch (e: DatabaseService.NotFoundException) {
            //do nothing since this means that the dataset is not in the database which is the same as having it removed
        }
    }

    override suspend fun putRepresentativePicture(picture: Uri, category: Category) {
        initialize()
        if (downloadedCategories.contains(category.id)) {
            throw IllegalStateException("category ${category.id} is downloaded and hence it's dataset is read only")
        }
        try {
            this.databaseService.putRepresentativePicture(picture, category)
        } catch (e: DatabaseService.NotFoundException) {
            throw e
        }
    }

    /**
     * Sets a categorized picture as the representative picture of the category it is assigned to,
     * removing it from the pictures of the category in the process.
     *
     * @param picture - the categorized picture to set as representative picture
     * @throws DatabaseService.NotFoundException if the underlying database does not contain the specified picture
     */
    override suspend fun putRepresentativePicture(picture: CategorizedPicture) {
        initialize()
        if (downloadedCategories.contains(picture.category.id)) {
            throw IllegalStateException("category ${picture.category.id} is downloaded and hence it's dataset is read only")
        }
        this.databaseService.putRepresentativePicture(picture)
        removePicture(picture)
    }

    override suspend fun getDatasets(): Set<Dataset> {
        initialize()
        val offlineDatasets = offlineDatabaseService.getDatasets()
        val firestoreDatasets = this.databaseService.getDatasets()
            .filter { ds -> !downloadedDatasets.contains(ds.name) }
        return offlineDatasets.union(firestoreDatasets)
    }

    override suspend fun getDatasetNames(): Collection<String> {
        initialize()
        return getDatasets().map { ds -> ds.name }
    }

    override suspend fun getDatasetIds(): Set<Id> {
        initialize()
        return getDatasets().map { ds -> ds.id }.toSet()
    }

    override suspend fun removeCategoryFromDataset(dataset: Dataset, category: Category): Dataset {
        initialize()
        if (downloadedCategories.contains(category.id)) {
            throw IllegalStateException("category ${category.id} is downloaded and hence it's dataset is read only")
        }
        return try {
            this.databaseService.removeCategoryFromDataset(dataset, category)
        } catch (e: DatabaseService.NotFoundException) {
            throw e
        }
    }

    override suspend fun editDatasetName(dataset: Dataset, newName: String): Dataset {
        initialize()
        if (downloadedDatasets.contains(dataset.id)) {
            throw IllegalStateException("dataset ${dataset.id} is downloaded and hence it's dataset is read only")
        }
        return try {
            this.databaseService.editDatasetName(dataset, newName)
        } catch (e: DatabaseService.NotFoundException) {
            throw e
        }
    }

    override suspend fun addCategoryToDataset(dataset: Dataset, category: Category): Dataset {
        initialize()
        if (downloadedDatasets.contains(dataset.id)) {
            throw IllegalStateException("dataset ${dataset.id} is downloaded and hence it's dataset is read only")
        }
        return try {
            this.databaseService.addCategoryToDataset(dataset, category)
        } catch (e: DatabaseService.NotFoundException) {
            throw e
        }
    }

    private fun initializeRoomEntities(
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

    private fun initializeRoomCrossRefs(
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

    private fun initialize() {
        if (uninitialized) {
            val datasets = datasetDao.loadAll()
            val categories = datasets.flatMap { ds -> ds.categories }
            downloadedDatasets.addAll(datasets.map { ds -> ds.datasetWithoutCategories.datasetId })
            downloadedCategories.addAll(categories.map { cat -> cat.categoryId })
            downloadedPictures.addAll(categories.flatMap { cat ->
                categoryDao.loadAllPictures(cat.categoryId)?.pictures?.map { pic -> pic.pictureId }
                    ?: listOf()
            })
            downloadedReprPictures.addAll(categories.mapNotNull { cat ->
                categoryDao.loadRepresentativePicture(
                    cat.categoryId
                )?.picture?.pictureId
            })
            uninitialized = false
        }
    }
}