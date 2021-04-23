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

//@TestInstallIn(
//    components = [ SingletonComponent::class ],
//    replaces = [ DatabaseServiceModule::class ],
//)
//@Module
//object DatabaseServiceTestModule {
//    // TODO remove: dummy should in principle never be used in the app directly
//    // TODO: currently presenter/AuthenticationPresenter injects the dummy directly
//    @DummyDatabase
//    @Provides
//    fun provideDummyService() = DatabaseServiceModule.provideDummyService()
//    @DemoDatabase
//    @Provides
//    fun provideDemoService(app: FirebaseApp) = DatabaseServiceModule.provideDemoService(app)
//
//    /** override demo2 with scratch */
//    @Demo2Database
//    @Provides
//    fun provideDemo2Service() = DummyDatabaseService()//provideScratchService(app)
//
//    // Only firestore unit tests are allowed to use tests (and the actual database will be provided
//    // by the firestore emulator)
//    @ScratchDatabase
//    @Provides
//    fun provideScratchService(app: FirebaseApp) = DatabaseServiceModule.provideScratchService(app)
//}
//
//@TestInstallIn(
//    components = [ SingletonComponent::class ],
//    replaces = [ DatabaseManagementModule::class ],
//)
//@Module
//object DatabaseManagementTestModule {
//    @DummyDatabase
//    @Provides
//    fun provideDummyManagement(@DummyDatabase db: DatabaseService): DatabaseManagement = DummyDatabaseManagement(db)
//
//    @DemoDatabase
//    @Provides
//    fun provideDemoService(@DemoDatabase db: DatabaseService): DatabaseManagement = FirestoreDatabaseManagement(db as FirestoreDatabaseService)
//
//    @Demo2Database
//    @Provides
//    fun provideDemo2Service(@Demo2Database db: DatabaseService): DatabaseManagement =
//        DummyDatabaseManagement(db)
//
//    @ScratchDatabase
//    @Provides
//    fun provideScratchService(@ScratchDatabase db: DatabaseService): DatabaseManagement = FirestoreDatabaseManagement(db as FirestoreDatabaseService)
//}