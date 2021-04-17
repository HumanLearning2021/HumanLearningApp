package com.github.HumanLearning2021.HumanLearningApp.room

import android.net.Uri
import androidx.room.*

@Entity(
    tableName = "picture",
    foreignKeys = [ForeignKey(
        entity = RoomCategory::class,
        parentColumns = ["categoryId"],
        childColumns = ["categoryId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class RoomPicture(
    @PrimaryKey val pictureId: String,
    val uri: Uri,
    @ColumnInfo(index = true) val categoryId: String
)