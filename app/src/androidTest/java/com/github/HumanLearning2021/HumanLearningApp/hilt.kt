package com.github.HumanLearning2021.HumanLearningApp

import com.github.HumanLearning2021.HumanLearningApp.hilt.*
import com.google.firebase.FirebaseApp
import dagger.Module
import dagger.Provides
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
    fun provideOfflineDemoService() = DatabaseServiceModule.provideOfflineDemoService()

    @OfflineScratchDatabase
    @Provides
    fun provideOfflineScratchService() = DatabaseServiceModule.provideOfflineScratchService()
}