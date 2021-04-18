package com.github.HumanLearning2021.HumanLearningApp.room

import androidx.room.*

data class RoomHLDatabase (
    @Embedded val databaseWithoutDatasets: RoomDatabaseWithoutDatasets,
    @Relation(
        parentColumn = "databaseName",
        entityColumn = "datasetId",
        associateBy = Junction(RoomDatabaseDatasetsCrossRef::class)
    ) val datasets: List<RoomDatasetWithoutCategories>
)

@Dao
interface DatabaseDao {
    @Transaction
    @Query("SELECT * FROM HLDatabase")
    fun loadAll(): List<RoomHLDatabase>

    @Transaction
    @Query("SELECT * FROM HLDatabase WHERE databaseName = :databaseName LIMIT 1")
    fun loadByName(databaseName: String): RoomHLDatabase?

    @Insert
    fun insertAll(vararg refs: RoomDatabaseDatasetsCrossRef)

    @Insert
    fun insertAll(vararg databases: RoomDatabaseWithoutDatasets)

    @Delete
    fun delete(database: RoomDatabaseWithoutDatasets)

    @Delete
    fun delete(ref: RoomDatabaseDatasetsCrossRef)
}