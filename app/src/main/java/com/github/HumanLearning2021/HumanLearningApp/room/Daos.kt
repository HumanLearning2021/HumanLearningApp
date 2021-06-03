package com.github.HumanLearning2021.HumanLearningApp.room

import androidx.room.*
import com.github.HumanLearning2021.HumanLearningApp.model.User

/**
 * Data access object for everything concerning categories in on device storage
 */
@Dao
interface CategoryDao {
    @Query("SELECT * FROM category")
    suspend fun loadAll(): List<RoomCategory>

    @Query("SELECT * FROM category WHERE categoryId = :id")
    suspend fun loadById(id: String): RoomCategory?

    @Query("SELECT * FROM category WHERE name = :name")
    suspend fun loadByName(name: String): List<RoomCategory>

    @Transaction
    @Query("SELECT * FROM category WHERE categoryId = :categoryId")
    suspend fun loadAllPictures(categoryId: String): RoomCategorizedPictures?

    @Transaction
    @Query("SELECT * FROM picture WHERE pictureId = :pictureId")
    suspend fun loadPicture(pictureId: String): RoomPicture?

    @Transaction
    @Query("SELECT * FROM category WHERE categoryId = :categoryId LIMIT 1")
    suspend fun loadRepresentativePicture(categoryId: String): RoomRepresentativePicture?

    @Update
    suspend fun update(category: RoomCategory)

    @Insert
    suspend fun insertAll(vararg categories: RoomCategory)

    @Insert
    suspend fun insertAll(vararg pictures: RoomPicture)

    @Insert
    suspend fun insertAll(vararg pictures: RoomUnlinkedRepresentativePicture)

    @Delete
    suspend fun delete(category: RoomCategory)

    @Delete
    suspend fun delete(picture: RoomPicture)

    @Delete
    suspend fun delete(picture: RoomUnlinkedRepresentativePicture)
}

/**
 * Data access object for everything concerning datasets in on device storage
 */
@Dao
interface DatasetDao {
    @Transaction
    @Query("SELECT * FROM dataset")
    suspend fun loadAll(): List<RoomDataset>

    @Transaction
    @Query("SELECT * FROM dataset WHERE datasetId = :id LIMIT 1")
    suspend fun loadById(id: String): RoomDataset?

    @Transaction
    @Query("SELECT * FROM dataset WHERE name = :name")
    suspend fun loadByName(name: String): List<RoomDataset>

    @Query("SELECT * FROM datasetCrossRefs WHERE categoryId = :categoryId")
    suspend fun loadAll(categoryId: String): List<RoomDatasetCategoriesCrossRef>

    @Update
    suspend fun update(dataset: RoomDatasetWithoutCategories)

    @Insert
    suspend fun insertAll(vararg refs: RoomDatasetCategoriesCrossRef)

    @Insert
    suspend fun insertAll(vararg datasets: RoomDatasetWithoutCategories)

    @Delete
    suspend fun delete(dataset: RoomDatasetWithoutCategories)

    @Delete
    suspend fun delete(vararg refs: RoomDatasetCategoriesCrossRef)
}

/**
 * Data access object for everything concerning databases in on device storage
 */
@Dao
interface DatabaseDao {
    @Transaction
    @Query("SELECT * FROM HLDatabase")
    suspend fun loadAll(): List<RoomHLDatabase>

    @Transaction
    @Query("SELECT * FROM HLDatabase WHERE databaseName = :databaseName LIMIT 1")
    suspend fun loadByName(databaseName: String): RoomHLDatabase?

    @Insert
    suspend fun insertAll(vararg databases: RoomEmptyHLDatabase)

    @Insert
    suspend fun insertAll(vararg refs: RoomDatabaseDatasetsCrossRef)

    @Insert
    suspend fun insertAll(vararg refs: RoomDatabaseCategoriesCrossRef)

    @Insert
    suspend fun insertAll(vararg refs: RoomDatabasePicturesCrossRef)

    @Delete
    suspend fun delete(database: RoomEmptyHLDatabase)

    @Delete
    suspend fun delete(ref: RoomDatabaseDatasetsCrossRef)

    @Delete
    suspend fun delete(ref: RoomDatabaseCategoriesCrossRef)

    @Delete
    suspend fun delete(ref: RoomDatabasePicturesCrossRef)
}

/**
 * Data access object for everything concerning pictures in on device storage
 */
@Dao
interface PictureDao {
    @Query("SELECT * FROM picture")
    suspend fun loadAllPictures(): List<RoomPicture>

    @Query("SELECT * FROM RoomUnlinkedRepresentativePicture")
    suspend fun loadAllRepresentativePictures(): List<RoomUnlinkedRepresentativePicture>
}

/**
 * Data access object for everything concerning users in on device storage
 */
@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    suspend fun loadAll(): List<RoomUser>

    @Query("SELECT * FROM user WHERE userId = :id AND type = :type LIMIT 1")
    suspend fun load(id: String, type: User.Type): RoomUser?

    @Update
    suspend fun update(user: RoomUser)

    @Insert
    suspend fun insertAll(vararg users: RoomUser)

    @Delete
    suspend fun delete(user: RoomUser)
}