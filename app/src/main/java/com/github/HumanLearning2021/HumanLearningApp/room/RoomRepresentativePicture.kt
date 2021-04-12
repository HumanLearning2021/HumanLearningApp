package com.github.HumanLearning2021.HumanLearningApp.room

import androidx.room.Relation

class RoomRepresentativePicture(
    @Relation(parentColumn = "categoryId", entityColumn = "picture") val picture: RoomPicture
)