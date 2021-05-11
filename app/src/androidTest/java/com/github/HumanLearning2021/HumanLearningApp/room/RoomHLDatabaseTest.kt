package com.github.HumanLearning2021.HumanLearningApp.room

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.HumanLearning2021.HumanLearningApp.hilt.DatabaseManagementModule
import com.github.HumanLearning2021.HumanLearningApp.hilt.Demo2CachePictureRepository
import com.github.HumanLearning2021.HumanLearningApp.hilt.Demo2Database
import com.github.HumanLearning2021.HumanLearningApp.hilt.RoomDatabase
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseService
import com.github.HumanLearning2021.HumanLearningApp.offline.PictureRepository
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.*
import javax.inject.Inject

@UninstallModules(DatabaseManagementModule::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class RoomHLDatabaseTest {

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
    lateinit var db: RoomOfflineDatabase

    private lateinit var databaseDao: DatabaseDao
    private lateinit var datasetDao: DatasetDao

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Before
    fun createDb() {
        hiltRule.inject()
        demo2DbMgt = DatabaseManagementModule.provideDemo2Service(demo2DbService, repository)
        databaseDao = db.databaseDao()
        datasetDao = db.datasetDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    private fun getRandomString() = "${UUID.randomUUID()}"
    private fun getRandomDatasetWithoutCategories() =
        RoomDatasetWithoutCategories(getRandomString(), getRandomString())

    private fun getRandomDatabaseWithoutDatasets() =
        RoomEmptyHLDatabase(getRandomString())

    @Test
    fun works() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val hlDb1 = getRandomDatabaseWithoutDatasets()
        val hlDb2 = getRandomDatabaseWithoutDatasets()
        val ds1 = getRandomDatasetWithoutCategories()
        val ds2 = getRandomDatasetWithoutCategories()
        val ds3 = getRandomDatasetWithoutCategories()
        val ds4 = getRandomDatasetWithoutCategories()
        val ds5 = getRandomDatasetWithoutCategories()
        val refs = mutableListOf<RoomDatabaseDatasetsCrossRef>()
        refs.apply {
            add(RoomDatabaseDatasetsCrossRef(hlDb1.databaseName, ds1.datasetId))
            add(RoomDatabaseDatasetsCrossRef(hlDb1.databaseName, ds2.datasetId))
            add(RoomDatabaseDatasetsCrossRef(hlDb2.databaseName, ds3.datasetId))
            add(RoomDatabaseDatasetsCrossRef(hlDb2.databaseName, ds4.datasetId))
            add(RoomDatabaseDatasetsCrossRef(hlDb2.databaseName, ds5.datasetId))
        }

        databaseDao.insertAll(hlDb1, hlDb2)
        datasetDao.insertAll(ds1, ds2, ds3, ds4, ds5)
        databaseDao.insertAll(*refs.toTypedArray())

        val res1 = databaseDao.loadByName(hlDb1.databaseName)!!
        val res2 = databaseDao.loadByName(hlDb2.databaseName)!!

        assertThat(res1.datasets, hasSize(2))
        assertThat(res2.datasets, hasSize(3))
        assertThat(res1.emptyHLDatabase.databaseName, equalTo(hlDb1.databaseName))
        assertThat(res2.emptyHLDatabase.databaseName, equalTo(hlDb2.databaseName))
    }
}