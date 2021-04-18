package com.github.HumanLearning2021.HumanLearningApp.room

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(primaryKeys = ["databaseName", "datasetId"])
data class RoomDatabaseDatasetsCrossRef (
    val databaseName: String,
    @ColumnInfo(index = true) val datasetId: String,
)

@Entity(primaryKeys = ["databaseName", "categoryId"])
data class RoomDatabaseCategoriesCrossRef (
    val databaseName: String,
    @ColumnInfo(index = true) val categoryId: String,
)

@Entity(primaryKeys = ["databaseName", "pictureId"])
data class RoomDatabasePicturesCrossRef (
    val databaseName: String,
    @ColumnInfo(index = true) val pictureId: String,
)

@Entity(tableName = "datasetCrossRefs", primaryKeys = ["datasetId", "categoryId"])
data class RoomDatasetCategoriesCrossRef(
    val datasetId: String,
    @ColumnInfo(index = true) val categoryId: String,
)