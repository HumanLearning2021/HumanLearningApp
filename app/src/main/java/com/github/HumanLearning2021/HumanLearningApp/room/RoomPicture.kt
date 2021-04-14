package com.github.HumanLearning2021.HumanLearningApp.room

import android.net.Uri
import androidx.room.*

@Entity(
    foreignKeys = [ForeignKey(
        entity = RoomCategory::class,
        parentColumns = ["categoryId"],
        childColumns = ["assignedCategoryId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class RoomPicture(
    @PrimaryKey val uri: Uri,
    val assignedCategoryId: String
)