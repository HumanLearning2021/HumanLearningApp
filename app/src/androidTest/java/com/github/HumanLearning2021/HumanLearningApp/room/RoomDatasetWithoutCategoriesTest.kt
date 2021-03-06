package com.github.HumanLearning2021.HumanLearningApp.room

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.*

@RunWith(AndroidJUnit4::class)
class RoomDatasetWithoutCategoriesTest {
    private var dbName = "some name"
    private lateinit var db: RoomOfflineDatabase
    private lateinit var datasetDao: DatasetDao
    private lateinit var databaseDao: DatabaseDao

    @Before
    fun createDb() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, RoomOfflineDatabase::class.java).build()
        datasetDao = db.datasetDao()
        databaseDao = db.databaseDao()
        databaseDao.insertAll(RoomEmptyHLDatabase(dbName))
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    private fun getRandomString() = "${UUID.randomUUID()}"
    private fun getRandomDatasetWithoutCategories() =
        RoomDatasetWithoutCategories(getRandomString(), getRandomString())

    private fun asDataset(ds: RoomDatasetWithoutCategories) = RoomDataset(ds, listOf())

    @Test
    fun insertThenLoadDataset() = runBlocking {
        val testDataset = getRandomDatasetWithoutCategories()
        val ref = RoomDatabaseDatasetsCrossRef(dbName, testDataset.datasetId)

        datasetDao.insertAll(testDataset)
        databaseDao.insertAll(ref)

        val res = databaseDao.loadByName(dbName)!!.datasets
        assertThat(res, hasSize(1))
        assertThat(res.first(), equalTo(testDataset))
    }

    @Test
    fun insertThenLoadDatasets() = runBlocking {
        val numberOfDatasets = (2..50).random()
        val testDatasets = mutableListOf<RoomDatasetWithoutCategories>()
        val refs = mutableListOf<RoomDatabaseDatasetsCrossRef>()
        for (i in 0 until numberOfDatasets) {
            val ds = getRandomDatasetWithoutCategories()
            testDatasets.add(ds)
            refs.add(RoomDatabaseDatasetsCrossRef(dbName, ds.datasetId))
        }

        datasetDao.insertAll(*testDatasets.toTypedArray())
        databaseDao.insertAll(*refs.toTypedArray())
        val res = databaseDao.loadByName(dbName)!!.datasets

        assertThat(res, hasSize(numberOfDatasets))
        assertThat(res, containsInAnyOrder(*testDatasets.toTypedArray()))
    }

    @Test
    fun loadDatasetByIdWorks() = runBlocking {
        val numberOfDatasets = (1..10).random()
        val testDatasets = mutableListOf<RoomDatasetWithoutCategories>()
        for (i in 0 until numberOfDatasets) {
            testDatasets.add(getRandomDatasetWithoutCategories())
        }
        val loadDataset = testDatasets.random()

        datasetDao.insertAll(*testDatasets.toTypedArray())

        val res = datasetDao.loadById(loadDataset.datasetId)

        assertThat(res, equalTo(asDataset(loadDataset)))
    }

    @Test
    fun loadDatasetByNameWorks() = runBlocking {
        val commonName = "name"
        val numberOfDatasets = (3..10).random()
        val testDatasets = mutableListOf<RoomDatasetWithoutCategories>()
        for (i in 0 until numberOfDatasets) {
            testDatasets.add(getRandomDatasetWithoutCategories())
        }
        val loadDataset1 = testDatasets[0]
        val loadDataset2 = testDatasets[1]
        val updateDataset1 = RoomDatasetWithoutCategories(loadDataset1.datasetId, commonName)
        val updateDataset2 = RoomDatasetWithoutCategories(loadDataset2.datasetId, commonName)

        datasetDao.insertAll(*testDatasets.toTypedArray())
        datasetDao.update(updateDataset1)
        datasetDao.update(updateDataset2)

        val res = datasetDao.loadByName(commonName)

        assertThat(res, hasSize(2))
        assertThat(res, containsInAnyOrder(asDataset(updateDataset1), asDataset(updateDataset2)))
    }

    @Test
    fun deleteDatasetDeletesDataset() = runBlocking {
        val numberOfDatasets = (1..10).random()
        val testDatasets = mutableListOf<RoomDatasetWithoutCategories>()
        for (i in 0 until numberOfDatasets) {
            testDatasets.add(getRandomDatasetWithoutCategories())
        }

        datasetDao.insertAll(*testDatasets.toTypedArray())

        val deletionDataset = testDatasets.random()
        datasetDao.delete(deletionDataset)
        val res = databaseDao.loadByName(dbName)!!.datasets

        assertThat(res, CoreMatchers.not(contains(asDataset(deletionDataset))))
    }

    @Test
    fun updatingDatasetWorks() = runBlocking {
        val numberOfDatasets = (1..10).random()
        val testDatasets = mutableListOf<RoomDatasetWithoutCategories>()
        val refs = mutableListOf<RoomDatabaseDatasetsCrossRef>()
        for (i in 0 until numberOfDatasets) {
            val ds = getRandomDatasetWithoutCategories()
            testDatasets.add(ds)
            refs.add(RoomDatabaseDatasetsCrossRef(dbName, ds.datasetId))
        }

        datasetDao.insertAll(*testDatasets.toTypedArray())
        databaseDao.insertAll(*refs.toTypedArray())
        val toUpdateDataset = testDatasets.random()
        val updatedDataset =
            RoomDatasetWithoutCategories(toUpdateDataset.datasetId, getRandomString())
        datasetDao.update(updatedDataset)
        val res = databaseDao.loadByName(dbName)!!.datasets

        assertThat(res, hasSize(numberOfDatasets))
        assertThat(res, CoreMatchers.not(contains(toUpdateDataset)))
        assertThat(
            datasetDao.loadById(updatedDataset.datasetId),
            CoreMatchers.equalTo(asDataset(updatedDataset))
        )
    }
}