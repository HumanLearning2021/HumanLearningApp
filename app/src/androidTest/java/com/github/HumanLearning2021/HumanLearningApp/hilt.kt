package com.github.HumanLearning2021.HumanLearningApp

import android.content.Context
import androidx.room.Room
import com.github.HumanLearning2021.HumanLearningApp.hilt.*
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseService
import com.github.HumanLearning2021.HumanLearningApp.model.UniqueDatabaseManagement
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
    fun provideTestService(firestore: FirebaseFirestore) =
        DatabaseServiceModule.provideTestService(firestore)

    @ScratchDatabase
    @Provides
    fun provideScratchService(firestore: FirebaseFirestore) =
        DatabaseServiceModule.provideScratchService(firestore)

    @OfflineTestDatabase
    @Provides
    fun provideTestDatabase(
        @ApplicationContext context: Context,
        @GlobalDatabaseManagement uDb: UniqueDatabaseManagement,
        @RoomDatabase room: RoomOfflineDatabase,
    ) = DatabaseServiceModule.provideOfflineTestService(context, uDb, room)
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
        firestore: FirebaseFirestore,
        @DummyDatabase dummyDb: DatabaseManagement
    ): UniqueDatabaseManagement =
        DatabaseManagementModule.provideGlobalDatabaseManagement(context, room, firestore, dummyDb)
}
