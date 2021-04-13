package com.github.HumanLearning2021.HumanLearningApp.room

import androidx.room.Embedded
import androidx.room.Relation

data class RoomDataset(
    @Embedded val datasetWithoutCategories: RoomDatasetWithoutCategories,
    @Relation(
        parentColumn = "datasetId",
        entityColumn = "categoryId",
    ) val category: RoomCategory?
)