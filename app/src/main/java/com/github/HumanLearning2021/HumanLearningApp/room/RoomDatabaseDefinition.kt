package com.github.HumanLearning2021.HumanLearningApp.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [RoomCategory::class, RoomCategorizedPicture::class, RoomDataset::class, RoomUser::class, RoomRepresentativePicture::class, RoomDatasetWithCategories::class, RoomPicture::class], version = 1)
abstract class RoomDatabaseDefinition : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun categorizedPictureDao(): CategorizedPictureDao
    abstract fun representativePictureDao(): RepresentativePictureDao
    abstract fun datasetDao(): DatasetDao
    abstract fun userDao(): UserDao
}