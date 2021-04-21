package com.github.HumanLearning2021.HumanLearningApp.model

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.HumanLearning2021.HumanLearningApp.firestore.CachedFirestoreDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.offline.OfflineDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.room.RoomOfflineDatabase
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.IllegalStateException

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class UniqueDatabaseManagementTest {

    private lateinit var uDbMan: UniqueDatabaseManagement

    @Before
    fun setup() {
        uDbMan = UniqueDatabaseManagement()
    }

    @After
    fun teardown() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.cacheDir.deleteRecursively()
        RoomOfflineDatabase.getDatabase(context).clearAllTables()
        Thread.sleep(1000) //wait for above method to complete
    }

    @Test
    fun offlineAndFirestoreContainTheSameElements() = runBlocking {
        val dbName = "demo"
        val fDbMan: CachedFirestoreDatabaseManagement = uDbMan.accessDatabaseFromCloud(dbName)
        val oDbman: OfflineDatabaseManagement = uDbMan.downloadDatabase(dbName)
        assertThat(fDbMan.getDatasets().map { ds -> ds.id }, equalTo(oDbman.getDatasets().map { ds -> ds.id }))
        assertThat(fDbMan.getCategories().map { cat -> cat.id }, equalTo(oDbman.getCategories().map { cat -> cat.id }))
    }

    @Test
    fun offlineDatabaseThrowsIfNotDownloaded() = runBlocking {
        kotlin.runCatching {
            OfflineDatabaseManagement("demo")
        }.fold({
            Assert.fail("unexpected successful completion")
        }, {
            assertThat(it, Matchers.instanceOf(IllegalStateException::class.java))
        })
    }
}