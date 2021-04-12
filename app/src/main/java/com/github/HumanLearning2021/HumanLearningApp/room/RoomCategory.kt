package com.github.HumanLearning2021.HumanLearningApp.room

import androidx.room.*

@Entity(tableName = "category")
data class RoomCategory(
    @PrimaryKey val categoryId: String,
    val name: String,
    @ColumnInfo(index = true) val datasets: List<RoomDataset>
)

@Dao
interface CategoryDao {
    @Query("SELECT * FROM dataset")
    fun loadAll(): List<RoomCategory>

    @Query("SELECT * FROM dataset WHERE datasetId = :id")
    fun loadById(id: String): RoomCategory

    @Query("SELECT * FROM dataset WHERE name = :name")
    fun loadByName(name: String): List<RoomCategory>

    @Insert
    fun insertAll(vararg categories: RoomCategory)

    @Delete
    fun delete(category: RoomCategory)
}