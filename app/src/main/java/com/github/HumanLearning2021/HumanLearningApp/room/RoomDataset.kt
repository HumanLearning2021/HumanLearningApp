package com.github.HumanLearning2021.HumanLearningApp.room

import androidx.room.*

data class RoomDataset(
    @Embedded val datasetWithoutCategories: RoomDatasetWithoutCategories,
    @Relation(
        parentColumn = "datasetId",
        entityColumn = "categoryId",
        associateBy = Junction(RoomDatasetCategoriesCrossRef::class)
    ) val categories: List<RoomCategory>
)

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