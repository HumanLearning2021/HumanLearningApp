package com.github.HumanLearning2021.HumanLearningApp.room

import androidx.room.Embedded
import androidx.room.Relation

data class RoomDatasetWithCategories(
    @Embedded val dataset: RoomDataset,
    @Relation(
        parentColumn = "datasetId",
        entityColumn = "name"
    ) val categories: List<RoomCategory>
)