package com.github.HumanLearning2021.HumanLearningApp.room

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "databaseCrossRef", primaryKeys = ["databaseName", "datasetId"])
data class RoomDatabaseDatasetsCrossRef(
    val databaseName: String,
    @ColumnInfo(index = true) val datasetId: String,
)