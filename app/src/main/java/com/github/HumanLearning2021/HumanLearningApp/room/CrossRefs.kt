package com.github.HumanLearning2021.HumanLearningApp.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    primaryKeys = ["databaseName", "datasetId"],
    foreignKeys = [ForeignKey(
        entity = RoomEmptyHLDatabase::class,
        parentColumns = ["databaseName"],
        childColumns = ["databaseName"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class RoomDatabaseDatasetsCrossRef(
    @ColumnInfo(index = true) val databaseName: String,
    @ColumnInfo(index = true) val datasetId: String,
)

@Entity(
    primaryKeys = ["databaseName", "categoryId"],
    foreignKeys = [ForeignKey(
        entity = RoomEmptyHLDatabase::class,
        parentColumns = ["databaseName"],
        childColumns = ["databaseName"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class RoomDatabaseCategoriesCrossRef(
    @ColumnInfo(index = true) val databaseName: String,
    @ColumnInfo(index = true) val categoryId: String,
)

@Entity(
    primaryKeys = ["databaseName", "pictureId"],
    foreignKeys = [ForeignKey(
        entity = RoomEmptyHLDatabase::class,
        parentColumns = ["databaseName"],
        childColumns = ["databaseName"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class RoomDatabasePicturesCrossRef(
    val databaseName: String,
    @ColumnInfo(index = true) val pictureId: String,
)

@Entity(tableName = "datasetCrossRefs", primaryKeys = ["datasetId", "categoryId"])
data class RoomDatasetCategoriesCrossRef(
    val datasetId: String,
    @ColumnInfo(index = true) val categoryId: String,
)