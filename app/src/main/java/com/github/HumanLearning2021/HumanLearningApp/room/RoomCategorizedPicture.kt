package com.github.HumanLearning2021.HumanLearningApp.room

import androidx.room.Embedded
import androidx.room.Relation

data class RoomCategorizedPicture(
    @Embedded val category: RoomCategory,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "categoryId",
    ) val picture: RoomPicture
)