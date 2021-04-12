package com.github.HumanLearning2021.HumanLearningApp.room

import android.net.Uri
import androidx.room.*

@Entity(
    tableName = "picture",
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

@Dao
interface CategorizedPictureDao {
    @Query("SELECT * FROM picture WHERE ")
    fun getPicture(categoryId: String)

    @Insert
    fun insert(uri: Uri)

    @Delete
    fun delete(uri: Uri)
}

@Dao
interface RepresentativePictureDao {
    @Query("SELECT * FROM picture WHERE ")
    fun getRepresentativePicture(categoryId: String)

    @Insert
    fun insert(uri: Uri)

    @Delete
    fun delete(uri: Uri)
}