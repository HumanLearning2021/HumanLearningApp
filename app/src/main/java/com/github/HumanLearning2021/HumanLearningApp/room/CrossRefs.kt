package com.github.HumanLearning2021.HumanLearningApp.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

/**
 * Links a database to its datasets in on device storage
 * @property databaseName of the database
 * @property datasetId unique identifier of the dataset belonging to the database
 */
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

/**
 * Links a database to its categories in on device storage
 * @property databaseName of the database
 * @property categoryId unique identifier of the category belonging to the database
 */
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

/**
 * Links a database to its pictures in on device storage
 * @property databaseName of the database
 * @property pictureId unique identifier of the picture belonging to the database
 */
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

/**
 * Links a dataset to its categories in on device storage
 * @property datasetId unique identifier of the dataset
 * @property categoryId unique identifier of the category belonging  to the dataset
 */
@Entity(tableName = "datasetCrossRefs", primaryKeys = ["datasetId", "categoryId"])
data class RoomDatasetCategoriesCrossRef(
    val datasetId: String,
    @ColumnInfo(index = true) val categoryId: String,
)