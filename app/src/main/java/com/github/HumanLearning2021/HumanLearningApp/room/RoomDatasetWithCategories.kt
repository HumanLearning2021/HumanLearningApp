package com.github.HumanLearning2021.HumanLearningApp.room

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation

data class RoomDatasetWithCategories(
    @Embedded val dataset: RoomDataset,
    @Relation(
        parentColumn = "datasetId",
        entityColumn = "categoryId",
        entity = RoomCategory::class,
        projection = ["categoryId"]
    ) val category: String?
)