package com.github.HumanLearning2021.HumanLearningApp.room

import android.net.Uri
import androidx.room.*

@Entity(
    foreignKeys = [ForeignKey(
        entity = RoomCategory::class,
        parentColumns = ["categoryId"],
        childColumns = ["uri"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )]
)
data class RoomPicture(
    @PrimaryKey val uri: Uri,
    val categoryId: String
)