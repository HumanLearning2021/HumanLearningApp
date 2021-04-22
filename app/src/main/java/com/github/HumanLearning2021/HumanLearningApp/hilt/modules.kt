package com.github.HumanLearning2021.HumanLearningApp.hilt

import android.content.Context
import androidx.room.Room
import com.firebase.ui.auth.AuthUI
import com.github.HumanLearning2021.HumanLearningApp.firestore.FirestoreDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.firestore.FirestoreDatabaseService
import com.github.HumanLearning2021.HumanLearningApp.model.*
import com.github.HumanLearning2021.HumanLearningApp.offline.OfflineDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.offline.OfflineDatabaseService
import com.github.HumanLearning2021.HumanLearningApp.room.RoomOfflineDatabase
import com.google.firebase.FirebaseApp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DummyDatabase

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DemoDatabase

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class Demo2Database

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ScratchDatabase

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class OfflineDemoDatabase

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class OfflineScratchDatabase

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GlobalDatabaseManagement

@Module
@InstallIn(SingletonComponent::class)
object RoomDatabaseModule {
    @Provides
    @Singleton
    fun provideRoomDatabase(@ApplicationContext context: Context): RoomOfflineDatabase = Room.databaseBuilder(
        context.applicationContext,
        RoomOfflineDatabase::class.java,
        "general_offline_database"
    ).build()
}

@Module
@InstallIn(SingletonComponent::class)
object FirebaseAuthUIModule {
    @Provides
    fun provideAuthUI(app: FirebaseApp) = AuthUI.getInstance(app)
}

@Module
@InstallIn(SingletonComponent::class)
object FirebaseAppModule {
    @Provides
    fun provideApp() = FirebaseApp.getInstance()
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseServiceModule {
    @DummyDatabase
    @Provides
    @Singleton  // allows dummy data to persist across activities
    fun provideDummyService(): DatabaseService = DummyDatabaseService()

    @DemoDatabase
    @Provides
    fun provideDemoService(app: FirebaseApp): DatabaseService =
        FirestoreDatabaseService("demo", app)

    @Demo2Database
    @Provides
    fun provideDemo2Service(app: FirebaseApp): DatabaseService =
        FirestoreDatabaseService("demo2", app)

    @ScratchDatabase
    @Provides
    fun provideScratchService(app: FirebaseApp): DatabaseService =
        FirestoreDatabaseService("scratch", app)

    @OfflineDemoDatabase
    @Provides
    fun provideOfflineDemoService(@ApplicationContext context: Context, @GlobalDatabaseManagement uDb: UniqueDatabaseManagement): DatabaseService =
        runBlocking {
            uDb.downloadDatabase("demo")
            OfflineDatabaseService("demo", context, RoomDatabaseModule.provideRoomDatabase(context))
        }

    @OfflineScratchDatabase
    @Provides
    fun provideOfflineScratchService(@ApplicationContext context: Context, @GlobalDatabaseManagement uDb: UniqueDatabaseManagement): DatabaseService =
        runBlocking {
            uDb.downloadDatabase("offlineScratch")
            OfflineDatabaseService(
                "offlineScratch",
                context,
                RoomDatabaseModule.provideRoomDatabase(context)
            )
        }
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseManagementModule {
    @DummyDatabase
    @Provides
    fun provideDummyManagement(@DummyDatabase db: DatabaseService): DatabaseManagement = DummyDatabaseManagement(db)
    @DemoDatabase
    @Provides
    fun provideDemoService(@DemoDatabase db: DatabaseService): DatabaseManagement = FirestoreDatabaseManagement(db as FirestoreDatabaseService)
    @Demo2Database
    @Provides
    fun provideDemo2Service(@Demo2Database db: DatabaseService): DatabaseManagement = FirestoreDatabaseManagement(db as FirestoreDatabaseService)
    @ScratchDatabase
    @Provides
    fun provideScratchService(@ScratchDatabase db: DatabaseService): DatabaseManagement = FirestoreDatabaseManagement(db as FirestoreDatabaseService)
    @OfflineDemoDatabase
    @Provides
    fun provideOfflineDemoService(@OfflineDemoDatabase db: DatabaseService, @ApplicationContext context: Context): DatabaseManagement = OfflineDatabaseManagement(db as OfflineDatabaseService)
    @OfflineScratchDatabase
    @Provides
    fun provideOfflineScratchService(@OfflineScratchDatabase db: DatabaseService, @ApplicationContext context: Context): DatabaseManagement = OfflineDatabaseManagement(db as OfflineDatabaseService)
    @GlobalDatabaseManagement
    @Provides
    fun provideGlobalDatabaseManagement(@ApplicationContext context: Context, room: RoomOfflineDatabase): UniqueDatabaseManagement = UniqueDatabaseManagement(context, room)
}