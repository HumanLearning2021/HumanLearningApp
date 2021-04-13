package com.github.HumanLearning2021.HumanLearningApp.room

import androidx.room.*

@Entity(tableName = "dataset")
data class RoomDatasetWithoutCategories(
    @PrimaryKey val datasetId: String,
    val name: String,
)