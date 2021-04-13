package com.github.HumanLearning2021.HumanLearningApp.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@TypeConverters(RoomTypeConverters::class)
@Database(
    entities = [RoomCategory::class, RoomDatasetWithoutCategories::class, RoomUser::class, RoomPicture::class],
    version = 1,
    exportSchema = false
)
abstract class RoomOfflineDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun datasetDao(): DatasetDao
    abstract fun userDao(): UserDao
}