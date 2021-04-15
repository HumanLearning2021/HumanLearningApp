package com.github.HumanLearning2021.HumanLearningApp.room

import androidx.room.Relation

data class RoomRepresentativePicture(
    val categoryId: String,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "categoryId"
    ) val picture: RoomUnlinkedRepresentativePicture
)