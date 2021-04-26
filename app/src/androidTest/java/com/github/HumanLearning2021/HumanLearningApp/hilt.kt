package com.github.HumanLearning2021.HumanLearningApp

import com.github.HumanLearning2021.HumanLearningApp.firestore.FirestoreDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.firestore.FirestoreDatabaseService
import com.github.HumanLearning2021.HumanLearningApp.hilt.*
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseService
import com.github.HumanLearning2021.HumanLearningApp.model.DummyDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.DummyDatabaseService
import com.google.firebase.FirebaseApp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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
}