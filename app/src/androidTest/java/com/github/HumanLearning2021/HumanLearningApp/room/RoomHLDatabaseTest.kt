package com.github.HumanLearning2021.HumanLearningApp.room

import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.HumanLearning2021.HumanLearningApp.hilt.RoomDatabase
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.*
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class RoomHLDatabaseTest {

    @Inject
    @RoomDatabase
    lateinit var db: RoomOfflineDatabase

    private lateinit var databaseDao: DatabaseDao
    private lateinit var datasetDao: DatasetDao
    private lateinit var categoryDao: CategoryDao

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Before
    fun createDb() {
        hiltRule.inject()
        db.clearAllTables()
        databaseDao = db.databaseDao()
        datasetDao = db.datasetDao()
        categoryDao = db.categoryDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.clearAllTables()
        db.close()
    }

    private fun getRandomString() = "${UUID.randomUUID()}"
    private fun getRandomDatasetWithoutCategories() =
        RoomDatasetWithoutCategories(getRandomString(), getRandomString())

    private fun getRandomDatabaseWithoutDatasets() =
        RoomEmptyHLDatabase(getRandomString())

    @Test
    fun insertionWorks() = runBlocking {
        val hlDb1 = getRandomDatabaseWithoutDatasets()
        val hlDb2 = getRandomDatabaseWithoutDatasets()

        val ds1 = getRandomDatasetWithoutCategories()
        val ds2 = getRandomDatasetWithoutCategories()
        val ds3 = getRandomDatasetWithoutCategories()
        val ds4 = getRandomDatasetWithoutCategories()
        val ds5 = getRandomDatasetWithoutCategories()
        val dbDsRefs = mutableListOf<RoomDatabaseDatasetsCrossRef>()
        dbDsRefs.apply {
            add(RoomDatabaseDatasetsCrossRef(hlDb1.databaseName, ds1.datasetId))
            add(RoomDatabaseDatasetsCrossRef(hlDb1.databaseName, ds2.datasetId))
            add(RoomDatabaseDatasetsCrossRef(hlDb2.databaseName, ds3.datasetId))
            add(RoomDatabaseDatasetsCrossRef(hlDb2.databaseName, ds4.datasetId))
            add(RoomDatabaseDatasetsCrossRef(hlDb2.databaseName, ds5.datasetId))
        }

        val cat1 = RoomCategory(getRandomString(), getRandomString())
        val cat2 = RoomCategory(getRandomString(), getRandomString())
        val cat3 = RoomCategory(getRandomString(), getRandomString())
        val dsCatRefs = mutableListOf<RoomDatasetCategoriesCrossRef>()
        dsCatRefs.apply {
            add(RoomDatasetCategoriesCrossRef(ds1.datasetId, cat1.categoryId))
            add(RoomDatasetCategoriesCrossRef(ds1.datasetId, cat2.categoryId))
            add(RoomDatasetCategoriesCrossRef(ds2.datasetId, cat3.categoryId))
        }

        val pic1 = RoomPicture(getRandomString(), Uri.EMPTY, cat1.categoryId)
        val pic2 = RoomPicture(getRandomString(), Uri.EMPTY, cat2.categoryId)
        val pic3 = RoomPicture(getRandomString(), Uri.EMPTY, cat2.categoryId)
        val pic4 = RoomPicture(getRandomString(), Uri.EMPTY, cat3.categoryId)

        val rpUPic1 =
            RoomUnlinkedRepresentativePicture(getRandomString(), Uri.EMPTY, cat1.categoryId)
        val rpUPic2 =
            RoomUnlinkedRepresentativePicture(getRandomString(), Uri.EMPTY, cat2.categoryId)

        databaseDao.insertAll(hlDb1, hlDb2)
        datasetDao.insertAll(ds1, ds2, ds3, ds4, ds5)
        categoryDao.insertAll(cat1, cat2, cat3)
        categoryDao.insertAll(pic1, pic2, pic3, pic4)
        categoryDao.insertAll(rpUPic1, rpUPic2)
        databaseDao.insertAll(*dbDsRefs.toTypedArray())
        datasetDao.insertAll(*dsCatRefs.toTypedArray())

        val res1 = databaseDao.loadByName(hlDb1.databaseName)!!
        val res2 = databaseDao.loadByName(hlDb2.databaseName)!!

        assertThat(res1.datasets, hasSize(2))
        assertThat(res2.datasets, hasSize(3))
        assertThat(res1.emptyHLDatabase.databaseName, equalTo(hlDb1.databaseName))
        assertThat(res2.emptyHLDatabase.databaseName, equalTo(hlDb2.databaseName))
        assertThat(datasetDao.loadAll(), hasSize(5))
        assertThat(datasetDao.loadById(ds1.datasetId)!!.categories, hasSize(2))
        assertThat(datasetDao.loadById(ds2.datasetId)!!.categories, hasSize(1))
        assertThat(
            categoryDao.loadRepresentativePicture(cat1.categoryId)!!.picture,
            not(equalTo(null))
        )
        assertThat(
            categoryDao.loadRepresentativePicture(cat2.categoryId)!!.picture,
            not(equalTo(null))
        )
        assertThat(categoryDao.loadRepresentativePicture(cat3.categoryId)!!.picture, equalTo(null))
    }
}