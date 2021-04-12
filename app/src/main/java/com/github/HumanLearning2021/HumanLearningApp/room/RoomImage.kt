package com.github.HumanLearning2021.HumanLearningApp.room

import androidx.room.*
import androidx.room.ForeignKey.CASCADE

@Entity(
    tableName = "image",
    foreignKeys = [ForeignKey(
        entity = RoomCategorizedPicture::class,
        parentColumns = ["category"],
        childColumns = ["path"],
        onDelete = CASCADE
    )]
)
data class RoomImage(
    val path: String
)

@Dao
interface ImageDao {
    @Insert
    fun insertAll(vararg images: RoomImage)

    @Delete
    fun delete(image: RoomImage)
}