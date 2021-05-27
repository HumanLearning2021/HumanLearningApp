package com.github.HumanLearning2021.HumanLearningApp

import android.content.Context
import androidx.room.Room
import com.github.HumanLearning2021.HumanLearningApp.hilt.FirebaseFirestoreModule
import com.github.HumanLearning2021.HumanLearningApp.hilt.RoomDatabase
import com.github.HumanLearning2021.HumanLearningApp.hilt.RoomDatabaseModule
import com.github.HumanLearning2021.HumanLearningApp.room.RoomOfflineDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [FirebaseFirestoreModule::class],
)
@Module
class FirebaseFirestoreTestModule {
    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore {
        FirebaseFirestore.getInstance()
            .terminate() //TODO("Find out why it is initialized before this instead of just terminating it before restarting")
        val db = FirebaseFirestore.getInstance()
        db.useEmulator("10.0.2.2", 8080)
        val settings = FirebaseFirestoreSettings.Builder().setPersistenceEnabled(false).build()
        db.firestoreSettings = settings
        return db
    }
}

@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RoomDatabaseModule::class],
)
@Module
object RoomDatabaseTestModule {
    @RoomDatabase
    @Provides
    fun provideRoomTestDatabase(@ApplicationContext context: Context): RoomOfflineDatabase =
        Room.databaseBuilder(
            context.applicationContext,
            RoomOfflineDatabase::class.java,
            "general_offline_database"
        ).build()
}
