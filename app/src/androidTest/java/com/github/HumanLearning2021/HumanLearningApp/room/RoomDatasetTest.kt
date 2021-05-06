package com.github.HumanLearning2021.HumanLearningApp.room

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.*

@RunWith(AndroidJUnit4::class)
class RoomDatasetTest {
    private lateinit var db: RoomOfflineDatabase
    private lateinit var categoryDao: CategoryDao
    private lateinit var datasetDao: DatasetDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, RoomOfflineDatabase::class.java).build()
        categoryDao = db.categoryDao()
        datasetDao = db.datasetDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    private fun getRandomString() = "${UUID.randomUUID()}"
    private fun getRandomCategory(name: String = getRandomString()) =
        RoomCategory(getRandomString(), name)

    private fun getRandomDatasetWithoutCategories() =
        RoomDatasetWithoutCategories(getRandomString(), getRandomString())

    private fun asDataset(ds: RoomDatasetWithoutCategories) = RoomDataset(ds, listOf())
    private fun asDatasets(dss: List<RoomDatasetWithoutCategories>) =
        dss.map { ds -> asDataset(ds) }

    @Test
    fun addCategoriesToDatasetThenLoadThem() {
        val categories = listOf(getRandomCategory(), getRandomCategory(), getRandomCategory())
        val dataset = getRandomDatasetWithoutCategories()
        val crossRefs = mutableListOf<RoomDatasetCategoriesCrossRef>()
        for (c in categories) {
            crossRefs.add(RoomDatasetCategoriesCrossRef(dataset.datasetId, c.categoryId))
        }

        categoryDao.insertAll(*categories.toTypedArray())
        datasetDao.insertAll(dataset)
        datasetDao.insertAll(*crossRefs.toTypedArray())

        val res = datasetDao.loadById(dataset.datasetId)!!.categories

        assertThat(res, containsInAnyOrder(*categories.toTypedArray()))
    }

    @Test
    fun removeCategoryFromDataset() {
        val categories = listOf(getRandomCategory(), getRandomCategory(), getRandomCategory())
        val dataset = getRandomDatasetWithoutCategories()
        val crossRefs = mutableListOf<RoomDatasetCategoriesCrossRef>()
        for (c in categories) {
            crossRefs.add(RoomDatasetCategoriesCrossRef(dataset.datasetId, c.categoryId))
        }

        categoryDao.insertAll(*categories.toTypedArray())
        datasetDao.insertAll(dataset)
        datasetDao.insertAll(*crossRefs.toTypedArray())

        val check = datasetDao.loadById(dataset.datasetId)!!.categories
        require(check.containsAll(categories))

        datasetDao.delete(RoomDatasetCategoriesCrossRef(dataset.datasetId, categories.first().categoryId))

        val res = datasetDao.loadById(dataset.datasetId)!!.categories

        val remainingCategories = listOf(categories[1], categories[2])
        assertThat(res, hasSize(remainingCategories.size))
        assertThat(res, not(contains(categories.first())))
    }
}