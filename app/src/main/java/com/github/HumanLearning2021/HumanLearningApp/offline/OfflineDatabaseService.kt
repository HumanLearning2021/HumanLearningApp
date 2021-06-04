package com.github.HumanLearning2021.HumanLearningApp.offline

import android.content.Context
import android.net.Uri
import com.github.HumanLearning2021.HumanLearningApp.model.*
import com.github.HumanLearning2021.HumanLearningApp.offline.OfflineConverters.fromCategory
import com.github.HumanLearning2021.HumanLearningApp.offline.OfflineConverters.fromDataset
import com.github.HumanLearning2021.HumanLearningApp.offline.OfflineConverters.fromPicture
import com.github.HumanLearning2021.HumanLearningApp.offline.OfflineConverters.fromUser
import com.github.HumanLearning2021.HumanLearningApp.room.*
import com.google.firebase.auth.FirebaseUser
import java.util.*

/**
 * Class used to interact with the database save locally on the device
 * @param dbName name of the database
 * @param context the application context
 * @param room Room database of the application
 */
class OfflineDatabaseService internal constructor(
    val dbName: String,
    val context: Context,
    val room: RoomOfflineDatabase
) : DatabaseService {

    private val pictureCache: PictureCache = PictureCache(dbName, context)
    private val databaseDao: DatabaseDao = room.databaseDao()
    private val datasetDao: DatasetDao = room.datasetDao()
    private val categoryDao: CategoryDao = room.categoryDao()
    private val userDao: UserDao = room.userDao()

    private fun getID() = "${UUID.randomUUID()}"

    /**
     * A function to clear the content of the database from the device
     */
    suspend fun clear() {
        room.databaseDao().delete(RoomEmptyHLDatabase(dbName))
        pictureCache.clear()
    }

    override suspend fun getPicture(category: Category): CategorizedPicture? {
        categoryDao.loadById(category.id) ?: throw DatabaseService.NotFoundException(category.id)
        val randomId =
            categoryDao.loadAllPictures(category.id)?.pictures?.map { pic -> pic.pictureId }
                ?.random()
        val picture = randomId?.let { categoryDao.loadPicture(it) }
        return picture?.let { fromPicture(picture, categoryDao) }
    }

    override suspend fun getPicture(pictureId: Id): CategorizedPicture? =
        categoryDao.loadPicture(pictureId)?.let { fromPicture(it, categoryDao) }


    override suspend fun getPictureIds(category: Category): List<String> {
        val pics =
            categoryDao.loadAllPictures(category.id) ?: throw DatabaseService.NotFoundException(
                category.id
            )
        return pics.pictures.map { p -> p.pictureId }
    }

    override suspend fun getRepresentativePicture(categoryId: Id): CategorizedPicture? =
        categoryDao.loadRepresentativePicture(categoryId)?.let { fromPicture(it, categoryDao) }

    override suspend fun putPicture(picture: Uri, category: Category): CategorizedPicture {
        val cat = categoryDao.loadById(category.id) ?: throw DatabaseService.NotFoundException(
            category.id
        )
        val uri = pictureCache.savePicture(picture).second
        val pic = RoomPicture(getID(), uri, cat.categoryId)
        val ref = RoomDatabasePicturesCrossRef(dbName, pic.pictureId)
        categoryDao.insertAll(pic)
        databaseDao.insertAll(ref)
        return fromPicture(pic, categoryDao)
    }

    override suspend fun getCategory(id: Id): Category? =
        categoryDao.loadById(id)?.let { fromCategory(it) }

    override suspend fun putCategory(categoryName: String): Category {
        val cat = RoomCategory(getID(), categoryName)
        val ref = RoomDatabaseCategoriesCrossRef(dbName, cat.categoryId)
        categoryDao.insertAll(cat)
        databaseDao.insertAll(ref)
        return fromCategory(cat)
    }

    override suspend fun getCategories(): Set<Category> {
        return databaseDao.loadByName(dbName)?.categories?.map { c -> fromCategory(c) }?.toSet()
            ?: setOf()
    }

    override suspend fun getAllPictures(category: Category): Set<CategorizedPicture> {
        val cats =
            categoryDao.loadAllPictures(category.id) ?: throw DatabaseService.NotFoundException(
                category.id
            )
        return cats.pictures.map { p -> fromPicture(p, categoryDao) }.toSet()
    }

    override suspend fun removeCategory(category: Category) {
        categoryDao.delete(fromCategory(category))
        val dbRef = RoomDatabaseCategoriesCrossRef(dbName, category.id)
        val dsRefs = datasetDao.loadAll(category.id)
        databaseDao.delete(dbRef)
        datasetDao.delete(*dsRefs.toTypedArray())
    }

    override suspend fun removePicture(picture: CategorizedPicture) {
        categoryDao.loadPicture(picture.id)?.let { pic ->
            val ref = RoomDatabasePicturesCrossRef(dbName, pic.pictureId)
            pictureCache.deletePicture(pic.pictureId)
            categoryDao.delete(pic)
            databaseDao.delete(ref)
        }
    }

    override suspend fun putDataset(name: String, categories: Set<Category>): Dataset {
        val id = getID()
        val ds = RoomDatasetWithoutCategories(id, name)
        val dsRefs = mutableListOf<RoomDatasetCategoriesCrossRef>()
        categories.forEach { cat ->
            dsRefs.add(RoomDatasetCategoriesCrossRef(id, cat.id))
        }
        val dbRef = RoomDatabaseDatasetsCrossRef(dbName, id)
        val cats = categories.map { c -> fromCategory(c) }
        datasetDao.insertAll(ds)
        datasetDao.insertAll(*dsRefs.toTypedArray())
        databaseDao.insertAll(dbRef)
        return fromDataset(RoomDataset(ds, cats))
    }

    override suspend fun getDataset(id: Id): Dataset? =
        datasetDao.loadById(id)?.let { fromDataset(it) }


    override suspend fun deleteDataset(id: Id) {
        datasetDao.loadById(id)?.let { ds ->
            val dsRefs = datasetDao.loadAll(ds.datasetWithoutCategories.datasetId)
            val dbRef = RoomDatabaseDatasetsCrossRef(dbName, ds.datasetWithoutCategories.datasetId)
            datasetDao.delete(ds.datasetWithoutCategories)
            datasetDao.delete(*dsRefs.toTypedArray())
            databaseDao.delete(dbRef)
        }
    }

    override suspend fun putRepresentativePicture(picture: Uri, category: Category) {
        val cat = categoryDao.loadById(category.id) ?: throw DatabaseService.NotFoundException(
            category.id
        )
        categoryDao.insertAll(RoomUnlinkedRepresentativePicture(getID(), picture, cat.categoryId))
    }

    override suspend fun putRepresentativePicture(picture: CategorizedPicture) {
        putRepresentativePicture(picture.picture, picture.category)
        categoryDao.loadPicture(picture.id)?.let { categoryDao.delete(it) }
    }

    override suspend fun getDatasets(): Set<Dataset> {
        return databaseDao.loadByName(dbName)?.datasets?.mapNotNull { d ->
            datasetDao.loadById(
                d.datasetId
            )?.let { fromDataset(it) }
        }?.toSet() ?: setOf()
    }

    override suspend fun removeCategoryFromDataset(
        dataset: Dataset,
        category: Category
    ): Dataset {
        datasetDao.delete(RoomDatasetCategoriesCrossRef(dataset.id, category.id))
        val ds =
            datasetDao.loadById(dataset.id) ?: throw DatabaseService.NotFoundException(dataset.id)
        return fromDataset(ds)
    }

    override suspend fun editDatasetName(dataset: Dataset, newName: String): Dataset {
        val ds =
            datasetDao.loadById(dataset.id) ?: throw DatabaseService.NotFoundException(dataset.id)
        val updatedDs = RoomDatasetWithoutCategories(ds.datasetWithoutCategories.datasetId, newName)
        datasetDao.update(updatedDs)
        return fromDataset(RoomDataset(updatedDs, ds.categories))
    }

    override suspend fun addCategoryToDataset(
        dataset: Dataset,
        category: Category
    ): Dataset {
        val ds =
            datasetDao.loadById(dataset.id) ?: throw DatabaseService.NotFoundException(dataset.id)
        val cat = categoryDao.loadById(category.id) ?: throw DatabaseService.NotFoundException(
            category.id
        )
        datasetDao.insertAll(
            RoomDatasetCategoriesCrossRef(
                ds.datasetWithoutCategories.datasetId,
                cat.categoryId
            )
        )
        val updatedCats = ds.categories.toMutableList()
        updatedCats.add(cat)
        return fromDataset(RoomDataset(ds.datasetWithoutCategories, updatedCats))
    }

    override suspend fun updateUser(firebaseUser: FirebaseUser): User {
        throw IllegalStateException("No Users Downloaded yet")
    }

    override suspend fun setAdminAccess(firebaseUser: FirebaseUser, adminAccess: Boolean): User {
        throw IllegalStateException("No Admin In Offline Mode Error")
    }

    override suspend fun checkIsAdmin(user: User): Boolean {
        throw IllegalStateException("No Admin In Offline Mode Error")
    }


    override suspend fun getUser(type: User.Type, uid: String): User? {
        val user = userDao.load(uid, type) ?: return null
        return fromUser(user)
    }
}