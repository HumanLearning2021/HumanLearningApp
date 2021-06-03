package com.github.HumanLearning2021.HumanLearningApp.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * The Room database used by the application for on device storage
 */
@TypeConverters(RoomTypeConverters::class)
@Database(
    entities = [RoomCategory::class, RoomDatasetWithoutCategories::class, RoomUser::class, RoomPicture::class, RoomUnlinkedRepresentativePicture::class,
        RoomDatasetCategoriesCrossRef::class, RoomEmptyHLDatabase::class, RoomDatabaseDatasetsCrossRef::class, RoomDatabaseCategoriesCrossRef::class, RoomDatabasePicturesCrossRef::class],
    version = 1,
    exportSchema = false
)
abstract class RoomOfflineDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun datasetDao(): DatasetDao
    abstract fun userDao(): UserDao
    abstract fun databaseDao(): DatabaseDao
    abstract fun pictureDao(): PictureDao
}