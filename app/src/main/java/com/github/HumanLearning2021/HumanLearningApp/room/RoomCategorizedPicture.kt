package com.github.HumanLearning2021.HumanLearningApp.room

import androidx.room.Relation

data class RoomCategorizedPicture(
    @Relation(parentColumn = "categoryId", entityColumn = "picture") val picture: RoomPicture
)