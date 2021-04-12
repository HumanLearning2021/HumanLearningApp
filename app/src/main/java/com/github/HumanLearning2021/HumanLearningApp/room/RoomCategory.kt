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
    @Query("SELECT * FROM category WHERE categoryId = :categoryId")
    fun getPicture(categoryId: String): RoomCategorizedPicture

    @Transaction
    @Query("SELECT * FROM category WHERE categoryId = :categoryId LIMIT 1")
    fun getRepresentativePicture(categoryId: String): RoomRepresentativePicture

    @Insert
    fun insertAll(vararg categories: RoomCategory)

    @Delete
    fun delete(category: RoomCategory)
}