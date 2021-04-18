package com.github.HumanLearning2021.HumanLearningApp.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@TypeConverters(RoomTypeConverters::class)
@Database(
    entities = [RoomCategory::class, RoomDatasetWithoutCategories::class, RoomUser::class, RoomPicture::class, RoomUnlinkedRepresentativePicture::class,
        RoomDatasetCategoriesCrossRef::class, RoomDatabaseWithoutDatasets::class, RoomDatabaseDatasetsCrossRef::class],
    version = 1,
    exportSchema = false
)
abstract class RoomOfflineDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun datasetDao(): DatasetDao
    abstract fun userDao(): UserDao
    abstract fun databaseDao(): DatabaseDao

    companion object {
        // Singleton prevents multiple instances of the database opening at the same time
        @Volatile
        private var INSTANCE: RoomOfflineDatabase? = null

        fun getDatabase(context: Context): RoomOfflineDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RoomOfflineDatabase::class.java,
                    "general_offline_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}