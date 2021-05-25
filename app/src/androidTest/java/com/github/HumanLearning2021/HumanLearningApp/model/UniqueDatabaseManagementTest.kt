package com.github.HumanLearning2021.HumanLearningApp.model

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.HumanLearning2021.HumanLearningApp.hilt.GlobalDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.hilt.RoomDatabase
import com.github.HumanLearning2021.HumanLearningApp.offline.OfflineDatabaseService
import com.github.HumanLearning2021.HumanLearningApp.room.RoomOfflineDatabase
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.*
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class UniqueDatabaseManagementTest {

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

    private val dbName = "test"

    @Before
    fun setup() {
        hiltRule.inject()
        context.cacheDir.deleteRecursively()
        room.clearAllTables()
        Thread.sleep(1000) //wait for above method to complete
    }

    @Test
    fun getDatabaseNamesWorks() = runBlocking {
        assertThat(uDbMan.getDatabases(), hasItems(dbName))
    }

    @Test
    fun getDownloadedDatabaseNamesWorks() = runBlocking {
        uDbMan.downloadDatabase(dbName)
        assertThat(uDbMan.getDownloadedDatabases(), hasItem(dbName))
    }

    @Test
    fun offlineAndFirestoreDatabasesContainTheSameElements() = runBlocking {
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

    @Ignore("functionality removed for the time being")
    @Test
    fun offlineDatabaseThrowsIfNotDownloaded() = runBlocking {
        kotlin.runCatching {
            DefaultDatabaseManagement(OfflineDatabaseService(dbName, context, room))
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