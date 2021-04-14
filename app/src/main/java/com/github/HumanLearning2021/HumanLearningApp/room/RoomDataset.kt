package com.github.HumanLearning2021.HumanLearningApp.room

import androidx.room.*

data class RoomDataset(
    @Embedded val datasetWithoutCategories: RoomDatasetWithoutCategories,
    @Relation(
        parentColumn = "datasetId",
        entityColumn = "categoryId",
        associateBy = Junction(RoomDatasetCategoriesCrossRef::class)
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

    @Update
    fun update(dataset: RoomDataset)

    @Insert
    fun insertAll(vararg datasets: RoomDataset)

    @Insert
    fun insertCategories(datasetId: String, vararg categories: RoomCategory)

    @Delete
    fun delete(dataset: RoomDataset)

    @Delete
    fun delete(category: RoomCategory)
}