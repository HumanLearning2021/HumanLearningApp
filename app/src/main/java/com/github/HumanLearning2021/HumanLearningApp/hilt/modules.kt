package com.github.HumanLearning2021.HumanLearningApp.hilt

import com.firebase.ui.auth.AuthUI
import com.github.HumanLearning2021.HumanLearningApp.firestore.FirestoreDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.firestore.FirestoreDatabaseService
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseService
import com.github.HumanLearning2021.HumanLearningApp.model.DummyDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.DummyDatabaseService
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
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
annotation class EmulatedFirestore

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ProductionFirestore

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ProductionFirebaseApp

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class EmulationFirebaseApp

@Module
@InstallIn(SingletonComponent::class)
object FirebaseAppModule {
    @Provides
    @ProductionFirebaseApp
    fun provideApp(): FirebaseApp = FirebaseApp.getInstance()
}

@Module
@InstallIn(SingletonComponent::class)
object FirebaseAuthUIModule {
    @Provides
    fun provideAuthUI(@ProductionFirebaseApp app: FirebaseApp) = AuthUI.getInstance(app)
}

@Module
@InstallIn(SingletonComponent::class)
object EmulationModule {
    @Provides
    @ProductionFirestore
    fun provideNotEmulated(@ProductionFirebaseApp app: FirebaseApp): FirebaseFirestore = Firebase.firestore(app)

    @Provides
    @EmulatedFirestore
    @Singleton
    fun provideEmulated(): FirebaseFirestore {
        FirebaseFirestore.getInstance().terminate() //TODO("Find out why it is initialized before this instead of just terminating it before restarting")
        val db = FirebaseFirestore.getInstance()
        db.useEmulator("10.0.2.2", 8080)
        val settings = FirebaseFirestoreSettings.Builder().setPersistenceEnabled(false).build()
        db.firestoreSettings = settings
        return db
    }
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
    fun provideDemoService(@ProductionFirestore firestore: FirebaseFirestore): DatabaseService = FirestoreDatabaseService("demo", firestore)
    @Demo2Database
    @Provides
    fun provideDemo2Service( @ProductionFirestore firestore: FirebaseFirestore): DatabaseService = FirestoreDatabaseService("demo2", firestore)
    @ScratchDatabase
    @Provides
    fun provideScratchService(@ProductionFirestore firestore: FirebaseFirestore): DatabaseService = FirestoreDatabaseService("scratch", firestore)
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
}
