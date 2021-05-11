package com.github.HumanLearning2021.HumanLearningApp.model

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.HumanLearning2021.HumanLearningApp.hilt.*
import com.github.HumanLearning2021.HumanLearningApp.offline.OfflineDatabaseService
import com.github.HumanLearning2021.HumanLearningApp.offline.PictureRepository
import com.github.HumanLearning2021.HumanLearningApp.room.RoomOfflineDatabase
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@UninstallModules(DatabaseManagementModule::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class UniqueDatabaseManagementTest {

    @Inject
    @Demo2Database
    lateinit var demo2DbService: DatabaseService

    @BindValue
    @Demo2Database
    lateinit var demo2DbMgt: DatabaseManagement

    @Inject
    @Demo2CachePictureRepository
    lateinit var repository: PictureRepository

    @Inject
    @RoomDatabase
    lateinit var room: RoomOfflineDatabase

    @Inject
    @GlobalDatabaseManagement
    lateinit var uDbMan: UniqueDatabaseManagement

    @Inject
    @ApplicationContext
    lateinit var context: Context

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Before
    fun setup() {
        hiltRule.inject()
        demo2DbMgt = DatabaseManagementModule.provideDemo2Service(demo2DbService, repository)
        context.cacheDir.deleteRecursively()
        room.clearAllTables()
        Thread.sleep(1000) //wait for above method to complete
    }

    @Test
    fun getDatabaseNamesWorks() = runBlocking {
        assertThat(uDbMan.getDatabases(), hasItems("demo"))
    }

    @Test
    fun getDownloadedDatabaseNamesWorks() = runBlocking {
        uDbMan.downloadDatabase("demo")
        assertThat(uDbMan.getDownloadedDatabases(), hasItem("demo"))
    }

    @Test
    fun offlineAndFirestoreDatabasesContainTheSameElements() = runBlocking {
        val dbName = "demo"
        val fDbMan = uDbMan.accessCloudDatabase(dbName)
        val oDbman = uDbMan.downloadDatabase(dbName)
        assertThat(
            fDbMan.getDatasets().map { ds -> ds.id },
            equalTo(oDbman.getDatasets().map { ds -> ds.id })
        )
        assertThat(
            fDbMan.getCategories().map { cat -> cat.id },
            equalTo(oDbman.getCategories().map { cat -> cat.id })
        )
    }

    @Test
    fun offlineDatabaseThrowsIfNotDownloaded() = runBlocking {
        kotlin.runCatching {
            DefaultDatabaseManagement(OfflineDatabaseService("demo", context, room))
        }.fold({
            Assert.fail("unexpected successful completion")
        }, {
            assertThat(it, Matchers.instanceOf(IllegalStateException::class.java))
        })
    }

    @Test
    fun accessOfflineDatabaseReturnsCorrectType() = runBlocking {
        uDbMan.downloadDatabase("demo")
        assert(uDbMan.accessDatabase("demo") is DefaultDatabaseManagement)
    }
}