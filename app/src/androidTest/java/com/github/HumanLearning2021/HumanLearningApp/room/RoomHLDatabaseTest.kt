package com.github.HumanLearning2021.HumanLearningApp.room

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.HumanLearning2021.HumanLearningApp.App
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.*

@RunWith(AndroidJUnit4::class)
class RoomHLDatabaseTest {
    private lateinit var db: RoomOfflineDatabase
    private lateinit var databaseDao: DatabaseDao
    private lateinit var datasetDao: DatasetDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, RoomOfflineDatabase::class.java).build()
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
        RoomDatabaseWithoutDatasets(getRandomString())

    @Test
    fun works() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = RoomOfflineDatabase.getDatabase(context)
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
        assertThat(res1.databaseWithoutDatasets.databaseName, equalTo(hlDb1.databaseName))
        assertThat(res2.databaseWithoutDatasets.databaseName, equalTo(hlDb2.databaseName))
    }
}