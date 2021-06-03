package com.github.HumanLearning2021.HumanLearningApp.room

import android.content.Context
import android.net.Uri
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.*

@RunWith(AndroidJUnit4::class)
class RoomCategoryTest {
    private val dbName = "some name"
    private lateinit var db: RoomOfflineDatabase
    private lateinit var categoryDao: CategoryDao
    private lateinit var databaseDao: DatabaseDao

    @Before
    fun createDb() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, RoomOfflineDatabase::class.java).build()
        categoryDao = db.categoryDao()
        databaseDao = db.databaseDao()
        databaseDao.insertAll(RoomEmptyHLDatabase(dbName))
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    private fun getRandomString() = "${UUID.randomUUID()}"
    private fun getRandomCategory(name: String = getRandomString()) =
        RoomCategory(getRandomString(), name)

    private fun getRandomPicture(categoryId: String = getRandomString()) =
        RoomPicture(getRandomString(), Uri.parse(getRandomString()), categoryId)

    private fun getRandomRepresentativePicture(categoryId: String = getRandomString()) =
        RoomUnlinkedRepresentativePicture(
            getRandomString(),
            Uri.parse(getRandomString()),
            categoryId
        )

    @Test
    fun insertThenLoadCategories() = runBlocking {
        val numberOfCategories = (2..50).random()
        val testCategories = mutableListOf<RoomCategory>()
        val refs = mutableListOf<RoomDatabaseCategoriesCrossRef>()
        for (i in 0 until numberOfCategories) {
            val cat = getRandomCategory()
            testCategories.add(cat)
            refs.add(RoomDatabaseCategoriesCrossRef(dbName, cat.categoryId))
        }

        categoryDao.insertAll(*testCategories.toTypedArray())
        databaseDao.insertAll(*refs.toTypedArray())
        val res = databaseDao.loadByName(dbName)!!.categories

        assertThat(res, hasSize(numberOfCategories))
        assertThat(res, containsInAnyOrder(*testCategories.toTypedArray()))
    }

    @Test
    fun insertThenLoadAllPictures() = runBlocking {
        val numberOfPictures = (2..10).random()
        val category = getRandomCategory()
        val testPictures = mutableListOf<RoomPicture>()
        for (i in 0 until numberOfPictures) {
            val pic = getRandomPicture(category.categoryId)
            testPictures.add(pic)
        }

        categoryDao.insertAll(category)
        categoryDao.insertAll(*testPictures.toTypedArray())

        val res = categoryDao.loadAllPictures(category.categoryId)!!.pictures

        assertThat(res, hasSize(numberOfPictures))
        assertThat(res, containsInAnyOrder(*testPictures.toTypedArray()))
    }

    @Test
    fun loadPictureById() = runBlocking {
        val numberOfPictures = (2..10).random()
        val category = getRandomCategory()
        val testPictures = mutableListOf<RoomPicture>()
        for (i in 0 until numberOfPictures) {
            val pic = getRandomPicture(category.categoryId)
            testPictures.add(pic)
        }
        val loadPicture = testPictures.random()

        categoryDao.insertAll(category)
        categoryDao.insertAll(*testPictures.toTypedArray())

        val res = categoryDao.loadPicture(loadPicture.pictureId)

        assertThat(res, equalTo(loadPicture))
    }

    @Test
    fun insertThenLoadAllPicturesOfOneCategory() = runBlocking {
        val numberOfPictures = (2..10).random()
        val testPictures = mutableListOf<RoomPicture>()
        val tmpCats = mutableListOf<RoomCategory>()
        for (i in 0 until numberOfPictures) {
            val tmpCat = getRandomCategory()
            val pic = getRandomPicture(tmpCat.categoryId)
            testPictures.add(pic)
            tmpCats.add(tmpCat)
        }
        val category = getRandomCategory()
        val expectedPictures =
            listOf(getRandomPicture(category.categoryId), getRandomPicture(category.categoryId))
        testPictures.addAll(expectedPictures)

        categoryDao.insertAll(category)
        categoryDao.insertAll(*tmpCats.toTypedArray())
        categoryDao.insertAll(*testPictures.toTypedArray())

        val res = categoryDao.loadAllPictures(category.categoryId)!!.pictures

        assertThat(res, hasSize(expectedPictures.size))
        assertThat(res, containsInAnyOrder(*expectedPictures.toTypedArray()))
    }


    @Test
    fun loadRepresentativePicture() = runBlocking {
        val category = getRandomCategory()
        val representativePicture = getRandomRepresentativePicture(category.categoryId)

        categoryDao.insertAll(category)
        categoryDao.insertAll(representativePicture)

        val res = categoryDao.loadRepresentativePicture(category.categoryId)

        assertThat(
            res,
            equalTo(
                RoomRepresentativePicture(
                    representativePicture.categoryId,
                    representativePicture
                )
            )
        )
    }

    @Test
    fun loadCategoryByIdYieldsCorrectResult() = runBlocking {
        val numberOfCategories = (2..10).random()
        val testCategories = mutableListOf<RoomCategory>()
        for (i in 0 until numberOfCategories) {
            testCategories.add(getRandomCategory())
        }
        val loadCategory = testCategories.random()

        categoryDao.insertAll(*testCategories.toTypedArray())

        val res = categoryDao.loadById(loadCategory.categoryId)

        assertThat(res, equalTo(loadCategory))
    }

    @Test
    fun loadCategoryByNameYieldsCorrectResult() = runBlocking {
        val numberOfCategories = (2..10).random()
        val testCategories = mutableListOf<RoomCategory>()
        for (i in 0 until numberOfCategories) {
            testCategories.add(getRandomCategory())
        }
        val commonName = "name"
        val loadCategories = listOf(
            getRandomCategory(commonName),
            getRandomCategory(commonName),
            getRandomCategory(commonName)
        )
        testCategories.addAll(loadCategories)

        categoryDao.insertAll(*testCategories.toTypedArray())

        val res = categoryDao.loadByName(commonName)

        assertThat(res, hasSize(loadCategories.size))
        assertThat(res, containsInAnyOrder(*loadCategories.toTypedArray()))
    }

    @Test
    fun updateCategoryWorks() = runBlocking {
        val numberOfCategories = (1..10).random()
        val testCategories = mutableListOf<RoomCategory>()
        val refs = mutableListOf<RoomDatabaseCategoriesCrossRef>()
        for (i in 0 until numberOfCategories) {
            val cat = getRandomCategory()
            testCategories.add(cat)
            refs.add(RoomDatabaseCategoriesCrossRef(dbName, cat.categoryId))
        }

        categoryDao.insertAll(*testCategories.toTypedArray())
        databaseDao.insertAll(*refs.toTypedArray())
        val toUpdateCategory = testCategories.random()
        val updatedCategory = RoomCategory(toUpdateCategory.categoryId, getRandomString())
        categoryDao.update(updatedCategory)
        val res = databaseDao.loadByName(dbName)!!.categories

        assertThat(res, hasSize(numberOfCategories))
        assertThat(res, not(contains(toUpdateCategory)))
        assertThat(
            categoryDao.loadById(updatedCategory.categoryId),
            equalTo(updatedCategory)
        )
    }

    @Test
    fun deletePictureDeletesPicture() = runBlocking {
        val category = getRandomCategory()
        val numberOfPictures = (1..10).random()
        val testPictures = mutableListOf<RoomPicture>()
        for (i in 0 until numberOfPictures) {
            testPictures.add(getRandomPicture(category.categoryId))
        }

        categoryDao.insertAll(category)
        categoryDao.insertAll(*testPictures.toTypedArray())

        val deletionPicture = testPictures.random()
        val requestCat = deletionPicture.categoryId
        categoryDao.delete(deletionPicture)
        val res = categoryDao.loadAllPictures(requestCat)!!.pictures

        assertThat(res, hasSize(numberOfPictures - 1))
        assertThat(res, not(contains(deletionPicture)))
    }

    @Test
    fun deleteCategoryDeletesCategory() = runBlocking {
        val numberOfCategories = (1..10).random()
        val testCategories = mutableListOf<RoomCategory>()
        for (i in 0 until numberOfCategories) {
            testCategories.add(getRandomCategory())
        }

        categoryDao.insertAll(*testCategories.toTypedArray())

        val deletionCategory = testCategories.random()
        categoryDao.delete(deletionCategory)
        val res = databaseDao.loadByName(dbName)!!.categories

        assertThat(res, not(contains(deletionCategory)))
    }
}