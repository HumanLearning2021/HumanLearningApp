package com.github.HumanLearning2021.HumanLearningApp.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "database")
data class RoomDatabaseWithoutDatasets (
    @PrimaryKey val databaseName: String,
)