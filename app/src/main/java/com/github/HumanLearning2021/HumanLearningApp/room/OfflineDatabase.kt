package com.github.HumanLearning2021.HumanLearningApp.room

import androidx.room.*
import com.github.HumanLearning2021.HumanLearningApp.model.Category
import com.github.HumanLearning2021.HumanLearningApp.model.User

@Database(entities = [RoomCategory::class, RoomCategorizedPicture::class, RoomDataset::class, RoomUser::class], version = 1)
abstract class OfflineDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun categorizedPictureDao(): CategorizedPictureDao
    abstract fun datasetDao(): DatasetDao
    abstract fun userDao(): UserDao
}