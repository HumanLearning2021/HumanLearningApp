package com.github.HumanLearning2021.HumanLearningApp.room

import androidx.room.Entity

@Entity(primaryKeys = ["datasetId", "categoryId"])
data class RoomDatasetCategoriesCrossRef(
    val datasetId: String,
    val categoryId: String,
)