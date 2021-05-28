package com.github.HumanLearning2021.HumanLearningApp.room

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.github.HumanLearning2021.HumanLearningApp.model.User

/**
 * Represents an empty database in on device storage
 * @property databaseName of the database
 */
@Entity(tableName = "HLDatabase")
data class RoomEmptyHLDatabase(
    @PrimaryKey val databaseName: String,
)

/**
 * Represents an empty dataset in on device storage
 * @property datasetId unique identifier of the dataset
 * @property name of the dataset
 */
@Entity(tableName = "dataset")
data class RoomDatasetWithoutCategories(
    @PrimaryKey val datasetId: String,
    val name: String,
)

/**
 * Represents a category in on device storage
 * @property categoryId unique identifier of the category
 * @property name of the category
 */
@Entity(tableName = "category")
data class RoomCategory(
    @PrimaryKey val categoryId: String,
    val name: String,
)

/**
 * Represents a picture in on device storage
 * @property pictureId unique identifier of the picture
 * @property uri location of the underlying image
 * @property categoryId unique identifier of the category the picture belongs to
 */
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

/**
 * Represents a representative picture in on device storage
 * @property pictureId the unique identifier of the picture
 * @property uri location of the underlying image
 * @property categoryId the unique identifier of the category the picture represents
 */
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

/**
 * Represents a user in on device storage
 * @property userId unique identifier of th user
 * @property type of the user
 * @property displayName of the user
 * @property email of the user
 * @property isAdmin defines if the user has administrator privileges
 */
@Entity(tableName = "user", primaryKeys = ["userId", "type"])
data class RoomUser(
    val userId: String,
    val type: User.Type,
    val displayName: String?,
    val email: String?,
    val isAdmin: Boolean
)