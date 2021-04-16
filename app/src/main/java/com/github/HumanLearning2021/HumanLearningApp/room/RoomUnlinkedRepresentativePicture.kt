package com.github.HumanLearning2021.HumanLearningApp.room

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = RoomCategory::class,
        parentColumns = ["categoryId"],
        childColumns = ["categoryId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class RoomUnlinkedRepresentativePicture(
    @PrimaryKey val pictureId: String,
    val uri: Uri,
    @ColumnInfo(index = true) val categoryId: String,
)