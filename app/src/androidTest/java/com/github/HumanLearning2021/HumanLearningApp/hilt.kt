package com.github.HumanLearning2021.HumanLearningApp

import android.content.Context
import androidx.room.Room
import com.github.HumanLearning2021.HumanLearningApp.hilt.*
import com.github.HumanLearning2021.HumanLearningApp.model.UniqueDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.room.RoomOfflineDatabase
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn

@TestInstallIn(
    components = [ SingletonComponent::class ],
    replaces = [ DatabaseServiceModule::class ],
)
@Module
object DatabaseServiceTestModule {
    @DummyDatabase
    @Provides
    fun provideDummyService() = DatabaseServiceModule.provideDummyService()
    @DemoDatabase
    @Provides
    fun provideDemoService(@EmulatedFirestore firestore: FirebaseFirestore) = DatabaseServiceModule.provideDemoService(firestore)

    /** override demo2 with scratch */
    @Demo2Database
    @Provides
    fun provideDemo2Service(@EmulatedFirestore firestore: FirebaseFirestore) = provideScratchService(firestore)

    @ScratchDatabase
    @Provides
    fun provideScratchService(@EmulatedFirestore firestore: FirebaseFirestore) = DatabaseServiceModule.provideScratchService(firestore)

    @OfflineDemoDatabase
    @Provides
    fun provideDemoDatabase(@ApplicationContext context: Context, @GlobalDatabaseManagement uDb: UniqueDatabaseManagement) = DatabaseServiceModule.provideOfflineDemoService(context, uDb)
}

@TestInstallIn(
    components = [ SingletonComponent::class ],
    replaces = [ RoomDatabaseModule::class ],
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