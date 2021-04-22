package com.github.HumanLearning2021.HumanLearningApp

import android.content.Context
import androidx.room.Room
import com.github.HumanLearning2021.HumanLearningApp.hilt.*
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseService
import com.github.HumanLearning2021.HumanLearningApp.model.UniqueDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.room.RoomOfflineDatabase
import com.google.firebase.FirebaseApp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

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
    fun provideDemoService(app: FirebaseApp) = DatabaseServiceModule.provideDemoService(app)

    /** override demo2 with scratch */
    @Demo2Database
    @Provides
    fun provideDemo2Service(app: FirebaseApp) = provideScratchService(app)

    @ScratchDatabase
    @Provides
    fun provideScratchService(app: FirebaseApp) = DatabaseServiceModule.provideScratchService(app)

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