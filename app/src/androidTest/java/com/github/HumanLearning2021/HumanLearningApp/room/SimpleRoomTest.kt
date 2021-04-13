package com.github.HumanLearning2021.HumanLearningApp.room

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class SimpleRoomTest {
    private lateinit var db: RoomOfflineDatabase
    private lateinit var categoryDao: CategoryDao
    private lateinit var datasetDao: DatasetDao
    private lateinit var userDao: UserDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, RoomOfflineDatabase::class.java).build()
        categoryDao = db.categoryDao()
        datasetDao = db.datasetDao()
        userDao = db.userDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertDatasetsAndLoadAll() {
        val ds1 = RoomDatasetWithoutCategories("id1", "dataset 1")
        val ds2 = RoomDatasetWithoutCategories("id2", "dataset 2")

        datasetDao.insertAll(ds1, ds2)

        val res = datasetDao.loadAll()

        assertThat(res.size, equalTo(2))
        assert(res.contains(RoomDataset(ds1, null)))
        assert(res.contains(RoomDataset(ds2, null)))
    }

}