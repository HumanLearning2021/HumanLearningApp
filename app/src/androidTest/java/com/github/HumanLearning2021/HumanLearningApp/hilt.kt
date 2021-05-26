package com.github.HumanLearning2021.HumanLearningApp

import android.content.Context
import androidx.room.Room
import com.github.HumanLearning2021.HumanLearningApp.hilt.*
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseService
import com.github.HumanLearning2021.HumanLearningApp.model.DefaultDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.UniqueDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.offline.CachedDatabaseService
import com.github.HumanLearning2021.HumanLearningApp.offline.PictureCache
import com.github.HumanLearning2021.HumanLearningApp.room.RoomOfflineDatabase
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DatabaseServiceModule::class],
)
@Module
object DatabaseServiceTestModule {
    @DummyDatabase
    @Provides
    @Singleton
    fun provideDummyService() = DatabaseServiceModule.provideDummyService()

    @DemoDatabase
    @Provides
    fun provideDemoService(@EmulatedFirestore firestore: FirebaseFirestore) =
        DatabaseServiceModule.provideDemoService(firestore)

    /** override demo2 with scratch */
    @Demo2Database
    @Provides
    fun provideDemo2Service(@EmulatedFirestore firestore: FirebaseFirestore) =
        provideScratchService(firestore)

    @ScratchDatabase
    @Provides
    fun provideScratchService(@EmulatedFirestore firestore: FirebaseFirestore) =
        DatabaseServiceModule.provideScratchService(firestore)

    @OfflineDemoDatabase
    @Provides
    fun provideDemoDatabase(
        @ApplicationContext context: Context,
        @GlobalDatabaseManagement uDb: UniqueDatabaseManagement,
        @RoomDatabase room: RoomOfflineDatabase,
    ) = DatabaseServiceModule.provideOfflineDemoService(context, uDb, room)

    @CachedDemoDatabase
    @Provides
    fun provideCacheDemoDatabase(
        @DemoDatabase db: DatabaseService,
        @DemoCachePictureRepository cache: PictureCache
    ): DatabaseService = CachedDatabaseService(db, cache)
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

@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DatabaseManagementModule::class]
)
@Module
object DatabaseManagementTestModule {
    @DummyDatabase
    @Provides
    fun provideDummyDatabaseManagement(@DummyDatabase db: DatabaseService): DatabaseManagement =
        DatabaseManagementModule.provideDummyManagement(db)

    @DemoDatabase
    @Provides
    fun provideDemoDatabaseManagement(@DemoDatabase db: DatabaseService): DatabaseManagement =
        DatabaseManagementModule.provideDemoService(db)

    @CachedDemoDatabase
    @Provides
    fun provideCachedDemoDatabaseManagement(@DemoDatabase db: DatabaseService): DatabaseManagement =
        DefaultDatabaseManagement(db)

    @ScratchDatabase
    @Provides
    fun provideScratchDatabaseManagement(@ScratchDatabase db: DatabaseService): DatabaseManagement =
        DatabaseManagementModule.provideScratchService(db)

    @OfflineDemoDatabase
    @Provides
    fun provideOfflineDemoDatabaseManagement(@OfflineDemoDatabase db: DatabaseService): DatabaseManagement =
        DatabaseManagementModule.provideOfflineDemoService(db)

    @GlobalDatabaseManagement
    @Provides
    fun provideUniqueDatabaseManagement(
        @ApplicationContext context: Context,
        @RoomDatabase room: RoomOfflineDatabase,
        @EmulatedFirestore firestore: FirebaseFirestore,
        @DummyDatabase dummyDb: DatabaseManagement
    ): UniqueDatabaseManagement =
        DatabaseManagementModule.provideGlobalDatabaseManagement(context, room, firestore, dummyDb)
}