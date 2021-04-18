package com.github.HumanLearning2021.HumanLearningApp.room

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class RoomCategorizedPictures(
    val categoryId: String,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "categoryId",
    ) val pictures: List<RoomPicture>
)

data class RoomDataset(
    @Embedded val datasetWithoutCategories: RoomDatasetWithoutCategories,
    @Relation(
        parentColumn = "datasetId",
        entityColumn = "categoryId",
        associateBy = Junction(RoomDatasetCategoriesCrossRef::class)
    ) val categories: List<RoomCategory>
)

data class RoomHLDatabase (
    @Embedded val databaseWithoutDatasets: RoomDatabaseWithoutDatasets,
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

data class RoomRepresentativePicture(
    val categoryId: String,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "categoryId"
    ) val picture: RoomUnlinkedRepresentativePicture
)