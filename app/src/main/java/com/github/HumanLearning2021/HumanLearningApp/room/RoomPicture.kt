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
    @PrimaryKey val uri: Uri
)

data class RoomCategorizedPicture(
    @Embedded val category: RoomCategory,
    @Relation(parentColumn = "categoryId", entityColumn = "categorizedPictureCategoryId") val pictures: List<RoomPicture>
)

data class RoomRepresentativePicture(
    @Embedded val category: RoomCategory,
    @Relation(parentColumn = "categoryId", entityColumn = "representativePictureCategoryId") val picture: RoomPicture
)