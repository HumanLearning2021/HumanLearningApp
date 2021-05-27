package com.github.HumanLearning2021.HumanLearningApp

import android.content.Context
import androidx.room.Room
import com.github.HumanLearning2021.HumanLearningApp.hilt.*
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseService
import com.github.HumanLearning2021.HumanLearningApp.model.DefaultDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.UniqueDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.offline.CachedDatabaseService
import com.github.HumanLearning2021.HumanLearningApp.offline.PictureRepository
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

    @TestDatabase
    @Provides
    fun provideTestService(@EmulatedFirestore firestore: FirebaseFirestore) =
        DatabaseServiceModule.provideTestService(firestore)

    /** override prod with scratch */
    @ProdDatabase
    @Provides
    fun provideProdService(@EmulatedFirestore firestore: FirebaseFirestore) =
        provideScratchService(firestore)

    @ScratchDatabase
    @Provides
    fun provideScratchService(@EmulatedFirestore firestore: FirebaseFirestore) =
        DatabaseServiceModule.provideScratchService(firestore)

    @OfflineTestDatabase
    @Provides
    fun provideTestDatabase(
        @ApplicationContext context: Context,
        @GlobalDatabaseManagement uDb: UniqueDatabaseManagement,
        @RoomDatabase room: RoomOfflineDatabase,
    ) = DatabaseServiceModule.provideOfflineTestService(context, uDb, room)

    @CachedTestDatabase
    @Provides
    fun provideCacheTestDatabase(
        @TestDatabase db: DatabaseService,
        @TestCachePictureRepository cache: PictureRepository
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

    @TestDatabase
    @Provides
    fun provideTestDatabaseManagement(@TestDatabase db: DatabaseService): DatabaseManagement =
        DatabaseManagementModule.provideTestService(db)

    @CachedTestDatabase
    @Provides
    fun provideCachedTestDatabaseManagement(@TestDatabase db: DatabaseService): DatabaseManagement =
        DefaultDatabaseManagement(db)

    @ScratchDatabase
    @Provides
    fun provideScratchDatabaseManagement(@ScratchDatabase db: DatabaseService): DatabaseManagement =
        DatabaseManagementModule.provideScratchService(db)

    @OfflineTestDatabase
    @Provides
    fun provideOfflineTestDatabaseManagement(@OfflineTestDatabase db: DatabaseService): DatabaseManagement =
        DatabaseManagementModule.provideOfflineTestService(db)

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