package com.github.HumanLearning2021.HumanLearningApp.room

import androidx.room.*
import com.github.HumanLearning2021.HumanLearningApp.model.User

@Dao
interface CategoryDao {
    @Query("SELECT * FROM category WHERE categoryId = :id")
    fun loadById(id: String): RoomCategory?

    @Query("SELECT * FROM category WHERE name = :name")
    fun loadByName(name: String): List<RoomCategory>

    @Transaction
    @Query("SELECT * FROM category WHERE categoryId = :categoryId")
    fun loadAllPictures(categoryId: String): RoomCategorizedPictures?

    @Transaction
    @Query("SELECT * FROM picture WHERE pictureId = :pictureId")
    fun loadPicture(pictureId: String): RoomPicture?

    @Transaction
    @Query("SELECT * FROM category WHERE categoryId = :categoryId LIMIT 1")
    fun loadRepresentativePicture(categoryId: String): RoomRepresentativePicture?

    @Update
    fun update(category: RoomCategory)

    @Insert
    fun insertAll(vararg categories: RoomCategory)

    @Insert
    fun insertAll(vararg pictures: RoomPicture)

    @Insert
    fun insertAll(vararg pictures: RoomUnlinkedRepresentativePicture)

    @Delete
    fun delete(category: RoomCategory)

    @Delete
    fun delete(picture: RoomPicture)

    @Delete
    fun delete(picture: RoomUnlinkedRepresentativePicture)
}

@Dao
interface DatasetDao {
    @Transaction
    @Query("SELECT * FROM dataset")
    fun loadAll(): List<RoomDataset>

    @Transaction
    @Query("SELECT * FROM dataset WHERE datasetId = :id LIMIT 1")
    fun loadById(id: String): RoomDataset?

    @Transaction
    @Query("SELECT * FROM dataset WHERE name = :name")
    fun loadByName(name: String): List<RoomDataset>

    @Query("SELECT * FROM datasetCrossRefs WHERE categoryId = :categoryId")
    fun loadAll(categoryId: String): List<RoomDatasetCategoriesCrossRef>

    @Update
    fun update(dataset: RoomDatasetWithoutCategories)

    @Insert
    fun insertAll(vararg refs: RoomDatasetCategoriesCrossRef)

    @Insert
    fun insertAll(vararg datasets: RoomDatasetWithoutCategories)

    @Delete
    fun delete(dataset: RoomDatasetWithoutCategories)

    @Delete
    fun delete(vararg refs: RoomDatasetCategoriesCrossRef)
}

@Dao
interface DatabaseDao {
    @Transaction
    @Query("SELECT * FROM HLDatabase")
    fun loadAll(): List<RoomHLDatabase>

    @Transaction
    @Query("SELECT * FROM HLDatabase WHERE databaseName = :databaseName LIMIT 1")
    fun loadByName(databaseName: String): RoomHLDatabase?

    @Insert
    fun insertAll(vararg databases: RoomEmptyHLDatabase)

    @Insert
    fun insertAll(vararg refs: RoomDatabaseDatasetsCrossRef)

    @Insert
    fun insertAll(vararg refs: RoomDatabaseCategoriesCrossRef)

    @Insert
    fun insertAll(vararg refs: RoomDatabasePicturesCrossRef)

    @Delete
    fun delete(database: RoomEmptyHLDatabase)

    @Delete
    fun delete(ref: RoomDatabaseDatasetsCrossRef)

    @Delete
    fun delete(ref: RoomDatabaseCategoriesCrossRef)

    @Delete
    fun delete(ref: RoomDatabasePicturesCrossRef)
}

@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun loadAll(): List<RoomUser>

    @Query("SELECT * FROM user WHERE userId = :id AND type = :type LIMIT 1")
    fun load(id: String, type: User.Type): RoomUser?

    @Update
    fun update(user: RoomUser)

    @Insert
    fun insertAll(vararg users: RoomUser)

    @Delete
    fun delete(user: RoomUser)
}