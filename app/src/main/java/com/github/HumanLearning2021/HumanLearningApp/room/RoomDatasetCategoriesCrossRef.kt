package com.github.HumanLearning2021.HumanLearningApp.room

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "datasetCrossRefs", primaryKeys = ["datasetId", "categoryId"])
data class RoomDatasetCategoriesCrossRef(
    val datasetId: String,
    @ColumnInfo(index = true) val categoryId: String,
)