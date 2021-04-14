package com.github.HumanLearning2021.HumanLearningApp.room

import androidx.room.*

@Entity(tableName = "category")
data class RoomCategory(
    @PrimaryKey val categoryId: String,
    val name: String,
)

@Dao
interface CategoryDao {
    @Query("SELECT * FROM category")
    fun loadAll(): List<RoomCategory>

    @Query("SELECT * FROM category WHERE categoryId = :id")
    fun loadById(id: String): RoomCategory

    @Query("SELECT * FROM category WHERE name = :name")
    fun loadByName(name: String): List<RoomCategory>

    @Transaction
    @Query("SELECT * FROM category WHERE categoryId = :categoryId LIMIT 1")
    fun getPicture(categoryId: String): RoomCategorizedPicture

    @Transaction
    @Query("SELECT * FROM category WHERE categoryId = :categoryId")
    fun getAllPictures(categoryId: String): List<RoomCategorizedPicture>

    @Transaction
    @Query("SELECT * FROM category WHERE categoryId = :categoryId LIMIT 1")
    fun getRepresentativePicture(categoryId: String): RoomRepresentativePicture

    @Update
    fun update(category: RoomCategory)

    @Insert
    fun insertAll(vararg categories: RoomCategory)

    @Insert
    fun insert(picture: RoomCategorizedPicture)

    @Insert
    fun insert(picture: RoomRepresentativePicture)

    @Delete
    fun delete(category: RoomCategory)

    @Delete
    fun delete(picture: RoomCategorizedPicture)

    @Delete
    fun delete(picture: RoomRepresentativePicture)
}