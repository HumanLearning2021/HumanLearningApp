package com.github.HumanLearning2021.HumanLearningApp.room

import androidx.room.*
import com.github.HumanLearning2021.HumanLearningApp.model.Category

@Entity(tableName = "dataset")
data class RoomDataset(
    @PrimaryKey val datasetId: String,
    val name: String,
)

@Dao
interface DatasetDao {
    @Transaction
    @Query("SELECT * FROM dataset")
    fun loadAll(): List<RoomDatasetWithCategories>

    @Transaction
    @Query("SELECT * FROM dataset WHERE datasetId = :id LIMIT 1")
    fun loadById(id: String): RoomDatasetWithCategories

    @Transaction
    @Query("SELECT * FROM dataset WHERE name = :name")
    fun loadByName(name: String): List<RoomDatasetWithCategories>

    @Insert
    fun insertAll(vararg datasets: RoomDataset)

    @Insert
    fun insertAll(vararg categories: RoomCategory)

    @Delete
    fun delete(dataset: RoomDataset)

    @Delete
    fun delete(category: RoomCategory)
}