package com.github.HumanLearning2021.HumanLearningApp.room

import androidx.room.*

data class RoomDataset(
    @Embedded val datasetWithoutCategories: RoomDatasetWithoutCategories,
    @Relation(
        parentColumn = "datasetId",
        entityColumn = "categoryId",
    ) val category: RoomCategory?
)

@Dao
interface DatasetDao {
    @Transaction
    @Query("SELECT * FROM dataset")
    fun loadAll(): List<RoomDataset>

    @Transaction
    @Query("SELECT * FROM dataset WHERE datasetId = :id LIMIT 1")
    fun loadById(id: String): RoomDataset

    @Transaction
    @Query("SELECT * FROM dataset WHERE name = :name")
    fun loadByName(name: String): List<RoomDataset>

    @Insert
    fun insertAll(vararg datasetWithoutCategories: RoomDatasetWithoutCategories)

    @Insert
    fun insertAll(vararg categories: RoomCategory)

    @Delete
    fun delete(datasetWithoutCategories: RoomDatasetWithoutCategories)

    @Delete
    fun delete(category: RoomCategory)
}