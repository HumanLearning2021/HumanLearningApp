package com.github.HumanLearning2021.HumanLearningApp.room

import android.net.Uri
import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class RoomRepresentativePicture(
    val categoryId: String,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "categoryId",
        entity = RoomPicture::class,
        projection = ["uri"],
    ) val uri: Uri
)