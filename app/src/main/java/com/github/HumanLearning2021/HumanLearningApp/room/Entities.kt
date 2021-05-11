package com.github.HumanLearning2021.HumanLearningApp.room

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.github.HumanLearning2021.HumanLearningApp.model.User

@Entity(tableName = "HLDatabase")
data class RoomEmptyHLDatabase(
    @PrimaryKey val databaseName: String,
)

@Entity(tableName = "dataset")
data class RoomDatasetWithoutCategories(
    @PrimaryKey val datasetId: String,
    val name: String,
)

@Entity(tableName = "category")
data class RoomCategory(
    @PrimaryKey val categoryId: String,
    val name: String,
)

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

@Entity(tableName = "user", primaryKeys = ["userId", "type"])
data class RoomUser(
    val userId: String,
    val type: User.Type,
    val displayName: String?,
    val email: String?
)