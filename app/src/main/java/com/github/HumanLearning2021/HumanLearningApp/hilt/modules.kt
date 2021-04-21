package com.github.HumanLearning2021.HumanLearningApp.hilt

import com.firebase.ui.auth.AuthUI
import com.github.HumanLearning2021.HumanLearningApp.firestore.CachedFirestoreDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.firestore.FirestoreDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.firestore.FirestoreDatabaseService
import com.github.HumanLearning2021.HumanLearningApp.model.*
import com.github.HumanLearning2021.HumanLearningApp.offline.OfflineDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.offline.OfflineDatabaseService
import com.google.firebase.FirebaseApp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
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
    fun provideDemoService(app: FirebaseApp): DatabaseService = FirestoreDatabaseService("demo", app)
    @Demo2Database
    @Provides
    fun provideDemo2Service(app: FirebaseApp): DatabaseService = FirestoreDatabaseService("demo2", app)
    @ScratchDatabase
    @Provides
    fun provideScratchService(app: FirebaseApp): DatabaseService = FirestoreDatabaseService("scratch", app)
    @OfflineDemoDatabase
    @Provides
    fun provideOfflineDemoService(): DatabaseService = OfflineDatabaseService("demo")
    @OfflineScratchDatabase
    @Provides
    fun provideOfflineScratchService(): DatabaseService = OfflineDatabaseService("offlineScratch")
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
    fun provideDemo2Service(@Demo2Database db: DatabaseService): DatabaseManagement = CachedFirestoreDatabaseManagement("demo2")
    @ScratchDatabase
    @Provides
    fun provideScratchService(@ScratchDatabase db: DatabaseService): DatabaseManagement = FirestoreDatabaseManagement(db as FirestoreDatabaseService)
    @OfflineDemoDatabase
    @Provides
    fun provideOfflineDemoService(@OfflineDemoDatabase db: DatabaseService): DatabaseManagement {
        val name = (db as OfflineDatabaseService).dbName
        return OfflineDatabaseManagement(name)
    }
    @OfflineScratchDatabase
    @Provides
    fun provideOfflineScratchService(@OfflineScratchDatabase db: DatabaseService): DatabaseManagement {
        val name = (db as OfflineDatabaseService).dbName
        return OfflineDatabaseManagement(name)
    }
}
