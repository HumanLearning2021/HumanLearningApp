package com.github.HumanLearning2021.HumanLearningApp.room

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

/**
 * Defines the categorized pictures associated to a category in on device storage
 * @property categoryId of the category the pictures belong to
 * @property pictures list of all the pictures associated to the category
 */
data class RoomCategorizedPictures(
    val categoryId: String,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "categoryId",
    ) val pictures: List<RoomPicture>
)

/**
 * Defines a dataset in on device storage
 * @property datasetWithoutCategories the dataset without it's categories
 * @property categories contained in the dataset
 */
data class RoomDataset(
    @Embedded val datasetWithoutCategories: RoomDatasetWithoutCategories,
    @Relation(
        parentColumn = "datasetId",
        entityColumn = "categoryId",
        associateBy = Junction(RoomDatasetCategoriesCrossRef::class)
    ) val categories: List<RoomCategory>
)

/**
 * Defines a database in on device storage
 * @property emptyHLDatabase the database without any content
 * @property datasets contained in the database
 * @property categories contained in the database
 * @property pictures contained in the database
 */
data class RoomHLDatabase(
    @Embedded val emptyHLDatabase: RoomEmptyHLDatabase,
    @Relation(
        parentColumn = "databaseName",
        entityColumn = "datasetId",
        associateBy = Junction(RoomDatabaseDatasetsCrossRef::class)
    ) val datasets: List<RoomDatasetWithoutCategories>,
    @Relation(
        parentColumn = "databaseName",
        entityColumn = "categoryId",
        associateBy = Junction(RoomDatabaseCategoriesCrossRef::class)
    ) val categories: List<RoomCategory>,
    @Relation(
        parentColumn = "databaseName",
        entityColumn = "pictureId",
        associateBy = Junction(RoomDatabasePicturesCrossRef::class)
    ) val pictures: List<RoomPicture>,
)

/**
 * Defines a representative picture in on device storage
 * @property categoryId of the category the picture represents
 * @property picture underlying the representative picture. Null if the category has none
 */
data class RoomRepresentativePicture(
    val categoryId: String,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "categoryId"
    ) val picture: RoomUnlinkedRepresentativePicture?
)