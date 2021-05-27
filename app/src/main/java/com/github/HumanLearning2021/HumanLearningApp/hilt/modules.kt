package com.github.HumanLearning2021.HumanLearningApp.hilt

import android.content.Context
import android.net.Uri
import androidx.room.Room
import com.firebase.ui.auth.AuthUI
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.firestore.FirestoreDatabaseService
import com.github.HumanLearning2021.HumanLearningApp.model.*
import com.github.HumanLearning2021.HumanLearningApp.offline.CachedDatabaseService
import com.github.HumanLearning2021.HumanLearningApp.offline.OfflineDatabaseService
import com.github.HumanLearning2021.HumanLearningApp.offline.PictureCache
import com.github.HumanLearning2021.HumanLearningApp.room.RoomOfflineDatabase
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
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
annotation class ProductionDatabaseName

/** In-memory database for testing,
preloaded with known data.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DummyDatabase

/** Read-only database used in unit tests,
preloaded with known data in the Firebase emulator.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class TestDatabase

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CachedTestDatabase

/** Production database used by the app. */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ProdDatabase

/** Writable database used in unit tests. */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ScratchDatabase

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class OfflineTestDatabase

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class OfflineScratchDatabase

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GlobalDatabaseManagement

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RoomDatabase

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class TestCachePictureRepository

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ProdCachePictureRepository

@Module
@InstallIn(SingletonComponent::class)
object RoomDatabaseModule {
    @Provides
    @Singleton
    @RoomDatabase
    fun provideRoomDatabase(@ApplicationContext context: Context): RoomOfflineDatabase =
        Room.databaseBuilder(
            context.applicationContext,
            RoomOfflineDatabase::class.java,
            "general_offline_database"
        ).build()
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class EmulatedFirestore

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ProductionFirestore

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ProductionFirebaseApp

@Module
@InstallIn(SingletonComponent::class)
object DatabaseNameModule {
    @Provides
    @ProductionDatabaseName
    fun provideProductionDatabaseName(): String = "prod"
}

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
    fun provideNotEmulated(@ProductionFirebaseApp app: FirebaseApp): FirebaseFirestore =
        Firebase.firestore(app)

    @Provides
    @EmulatedFirestore
    @Singleton
    fun provideEmulated(): FirebaseFirestore {
        FirebaseFirestore.getInstance()
            .terminate() //TODO("Find out why it is initialized before this instead of just terminating it before restarting")
        val db = FirebaseFirestore.getInstance()
        db.useEmulator("10.0.2.2", 8080)
        val settings = FirebaseFirestoreSettings.Builder().setPersistenceEnabled(false).build()
        db.firestoreSettings = settings
        return db
    }
}

@Module
@InstallIn(SingletonComponent::class)
object PictureRepositoryModule {
    @Provides
    @TestCachePictureRepository
    fun provideTestCachePictureRepository(@ApplicationContext context: Context): PictureCache =
        PictureCache.applicationPictureCache("test", context)

    @Provides
    @ProdCachePictureRepository
    fun provideProdCachePictureRepository(@ApplicationContext context: Context): PictureCache =
        PictureCache.applicationPictureCache("prod", context)
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseServiceModule {
    @DummyDatabase
    @Provides
    @Singleton  // allows dummy data to persist across activities
    fun provideDummyService(): DatabaseService = DummyDatabaseService().apply {
        // Inject expected dummy data
        val resPrefix = "android.resource://com.github.HumanLearning2021.HumanLearningApp/"
        val forkUri =
            Uri.parse(resPrefix + R.drawable.fork)
        val forkRepUri = Uri.parse(resPrefix + R.drawable.fork_rep)
        val knifeUri =
            Uri.parse(resPrefix + R.drawable.knife)
        val knifeRepUri = Uri.parse(resPrefix + R.drawable.knife_rep)
        val spoonUri =
            Uri.parse(resPrefix + R.drawable.spoon)
        val spoonRepUri = Uri.parse(resPrefix + R.drawable.spoon_rep)
        runBlocking {
            // fork2 allows us to have a dataset with 4 categories without needing a new test picture
            val fork2 = putCategory("Fork2")
            val fork = putCategory("Fork")
            val knife = putCategory("Knife")
            val spoon = putCategory("Spoon")

            putDataset("kitchen utensils", setOf(fork, spoon, knife))
            putDataset("one category", setOf(fork))
            putDataset("two categories", setOf(fork, knife))
            putDataset("four categories", setOf(fork, knife, spoon, fork2))

            putPicture(forkUri, fork)
            putPicture(knifeUri, knife)
            putPicture(spoonUri, spoon)

            putRepresentativePicture(forkRepUri, fork)
            putRepresentativePicture(forkRepUri, fork2)
            putRepresentativePicture(knifeRepUri, knife)
            putRepresentativePicture(spoonRepUri, spoon)
        }
    }

    @OfflineTestDatabase
    @Provides
    fun provideOfflineTestService(
        @ApplicationContext context: Context,
        @GlobalDatabaseManagement uDb: UniqueDatabaseManagement,
        @RoomDatabase room: RoomOfflineDatabase,
    ): DatabaseService =
        runBlocking {
            uDb.downloadDatabase("test")
            OfflineDatabaseService("test", context, room)
        }

    @OfflineScratchDatabase
    @Provides
    fun provideOfflineScratchService(
        @ApplicationContext context: Context,
        @GlobalDatabaseManagement uDb: UniqueDatabaseManagement,
        @RoomDatabase room: RoomOfflineDatabase
    ): DatabaseService =
        runBlocking {
            uDb.downloadDatabase("scratch")
            OfflineDatabaseService(
                "scratch",
                context,
                room
            )
        }

    @TestDatabase
    @Provides
    fun provideTestService(@ProductionFirestore firestore: FirebaseFirestore): DatabaseService =
        FirestoreDatabaseService("test", firestore)

    @ProdDatabase
    @Provides
    fun provideProdService(
        @ProductionFirestore firestore: FirebaseFirestore,
        @ProdCachePictureRepository repository: PictureCache
    ): DatabaseService =
        CachedDatabaseService(FirestoreDatabaseService("prod", firestore), repository)

    @ScratchDatabase
    @Provides
    fun provideScratchService(@ProductionFirestore firestore: FirebaseFirestore): DatabaseService =
        FirestoreDatabaseService("scratch", firestore)
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseManagementModule {
    @DummyDatabase
    @Provides
    fun provideDummyManagement(@DummyDatabase db: DatabaseService): DatabaseManagement =
        DefaultDatabaseManagement(db)

    @TestDatabase
    @Provides
    fun provideTestService(@TestDatabase db: DatabaseService): DatabaseManagement =
        DefaultDatabaseManagement(db)

    @ProdDatabase
    @Provides
    fun provideProdService(
        @ProdDatabase db: DatabaseService,
    ): DatabaseManagement = DefaultDatabaseManagement(db)

    @ScratchDatabase
    @Provides
    fun provideScratchService(@ScratchDatabase db: DatabaseService): DatabaseManagement =
        DefaultDatabaseManagement(db)

    @OfflineTestDatabase
    @Provides
    fun provideOfflineTestService(@OfflineTestDatabase db: DatabaseService): DatabaseManagement =
        DefaultDatabaseManagement(db)

    @OfflineScratchDatabase
    @Provides
    fun provideOfflineScratchService(@OfflineScratchDatabase db: DatabaseService): DatabaseManagement =
        DefaultDatabaseManagement(db)

    @GlobalDatabaseManagement
    @Provides
    fun provideGlobalDatabaseManagement(
        @ApplicationContext context: Context,
        @RoomDatabase room: RoomOfflineDatabase,
        @ProductionFirestore firestore: FirebaseFirestore,
        @DummyDatabase dummyDb: DatabaseManagement,
    ): UniqueDatabaseManagement = UniqueDatabaseManagement(context, room, firestore, dummyDb)
}
