package com.github.HumanLearning2021.HumanLearningApp.room

import androidx.room.*

data class RoomHLDatabase (
    @Embedded val databaseWithoutDatasets: RoomDatabaseDatasetsCrossRef,
    @Relation(
        parentColumn = "databaseName",
        entityColumn = "datasetId",
        associateBy = Junction(RoomDatabaseDatasetsCrossRef::class)
    ) val datasets: List<RoomDataset>
)

@Dao
interface DatabaseDao {
    @Transaction
    @Query("SELECT * FROM `database` WHERE databaseName = :databaseName")
    fun loadAll(databaseName: String): RoomDataset

    @Insert
    fun insertAll(vararg refs: RoomDatabaseDatasetsCrossRef)

    @Insert
    fun insertAll(vararg databases: RoomDatabaseWithoutDatasets)

    @Delete
    fun delete(database: RoomDatabaseWithoutDatasets)

    @Delete
    fun delete(ref: RoomDatabaseDatasetsCrossRef)
}