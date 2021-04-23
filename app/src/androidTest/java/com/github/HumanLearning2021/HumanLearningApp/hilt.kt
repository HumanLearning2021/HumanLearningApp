package com.github.HumanLearning2021.HumanLearningApp

import com.github.HumanLearning2021.HumanLearningApp.hilt.*
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
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
    fun provideDemoService(@EmulatedFirestore firestore: FirebaseFirestore) = DatabaseServiceModule.provideDemoService(firestore)

    /** override demo2 with scratch */
    @Demo2Database
    @Provides
    fun provideDemo2Service(@EmulatedFirestore firestore: FirebaseFirestore) = provideScratchService(firestore)

    @ScratchDatabase
    @Provides
    fun provideScratchService(@EmulatedFirestore firestore: FirebaseFirestore) = DatabaseServiceModule.provideScratchService(firestore)
}