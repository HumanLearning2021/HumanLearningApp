package com.github.HumanLearning2021.HumanLearningApp.room

import android.content.Context
import android.net.Uri
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.hamcrest.Matchers.contains
import org.hamcrest.Matchers.hasSize
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.*

@RunWith(AndroidJUnit4::class)
class RoomCategoryTest {
    private lateinit var db: RoomOfflineDatabase
    private lateinit var categoryDao: CategoryDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, RoomOfflineDatabase::class.java).build()
        categoryDao = db.categoryDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    private fun getRandomString() = "${UUID.randomUUID()}"
    private fun getRandomCategory(name: String = getRandomString()) = RoomCategory(getRandomString(), name)
    private fun getRandomPicture(categoryId: String = getRandomString()) = RoomPicture(getRandomString(), Uri.parse(getRandomString()), categoryId)
    private fun getRandomRepresentativePicture(categoryId: String = getRandomString()) = RoomUnlinkedRepresentativePicture(getRandomString(), Uri.parse(getRandomString()), categoryId)

    @Test
    fun insertThenLoadCategories() {
        val numberOfCategories = (2..50).random()
        val testCategories = mutableListOf<RoomCategory>()
        for (i in 0 until numberOfCategories) {
            testCategories.add(getRandomCategory())
        }

        categoryDao.insertAll(*testCategories.toTypedArray())

        val res = categoryDao.loadAll()

        MatcherAssert.assertThat(res, Matchers.hasSize(numberOfCategories))
        MatcherAssert.assertThat(res, Matchers.containsInAnyOrder(*testCategories.toTypedArray()))
    }

    @Test
    fun insertThenLoadAllPictures() {
        val numberOfPictures = (2..10).random()
        val category = getRandomCategory()
        val testPictures = mutableListOf<RoomPicture>()
        for (i in 0 until numberOfPictures) {
            val pic = getRandomPicture(category.categoryId)
            testPictures.add(pic)
        }

        categoryDao.insertAll(category)
        categoryDao.insertAll(*testPictures.toTypedArray())

        val res = categoryDao.loadAllPictures(category.categoryId).pictures

        MatcherAssert.assertThat(res, Matchers.hasSize(numberOfPictures))
        MatcherAssert.assertThat(res, Matchers.containsInAnyOrder(*testPictures.toTypedArray()))
    }

    @Test
    fun insertThenLoadAllPicturesOfOneCategory() {
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

        val res = categoryDao.loadAllPictures(category.categoryId).pictures

        MatcherAssert.assertThat(res, Matchers.hasSize(expectedPictures.size))
        MatcherAssert.assertThat(res, Matchers.containsInAnyOrder(*expectedPictures.toTypedArray()))
    }


        @Test
    fun loadRepresentativePicture() {
        val category = getRandomCategory()
        val representativePicture = getRandomRepresentativePicture(category.categoryId)

        categoryDao.insertAll(category)
        categoryDao.insertAll(representativePicture)

        val res = categoryDao.loadRepresentativePicture(category.categoryId)

        MatcherAssert.assertThat(res, equalTo(RoomRepresentativePicture(representativePicture.categoryId, representativePicture)))
    }

    @Test
    fun loadCategoryByIdYieldsCorrectResult() {
        val numberOfCategories = (2..10).random()
        val testCategories = mutableListOf<RoomCategory>()
        for (i in 0 until numberOfCategories) {
            testCategories.add(getRandomCategory())
        }
        val loadCategory = testCategories.random()

        categoryDao.insertAll(*testCategories.toTypedArray())

        val res = categoryDao.loadById(loadCategory.categoryId)

        MatcherAssert.assertThat(res, equalTo(loadCategory))
    }

    @Test
    fun loadCategoryByNameYieldsCorrectResult() {
        val numberOfCategories = (2..10).random()
        val testCategories = mutableListOf<RoomCategory>()
        for (i in 0 until numberOfCategories) {
            testCategories.add(getRandomCategory())
        }
        val commonName = "name"
        val loadCategories = listOf(getRandomCategory(commonName), getRandomCategory(commonName), getRandomCategory(commonName))
        testCategories.addAll(loadCategories)

        categoryDao.insertAll(*testCategories.toTypedArray())

        val res = categoryDao.loadByName(commonName)

        MatcherAssert.assertThat(res, Matchers.hasSize(loadCategories.size))
        MatcherAssert.assertThat(res, Matchers.containsInAnyOrder(*loadCategories.toTypedArray()))
    }

    @Test
    fun updateCategoryWorks() {
        val numberOfCategories = (1..10).random()
        val testCategories = mutableListOf<RoomCategory>()
        for (i in 0 until numberOfCategories) {
            testCategories.add(getRandomCategory())
        }

        categoryDao.insertAll(*testCategories.toTypedArray())
        val toUpdateCategory = testCategories.random()
        val updatedCategory = RoomCategory(toUpdateCategory.categoryId, getRandomString())
        categoryDao.update(updatedCategory)
        val res = categoryDao.loadAll()

        MatcherAssert.assertThat(res, Matchers.hasSize(numberOfCategories))
        MatcherAssert.assertThat(res, CoreMatchers.not(Matchers.contains(toUpdateCategory)))
        MatcherAssert.assertThat(
            categoryDao.loadById(updatedCategory.categoryId),
            equalTo(updatedCategory)
        )
    }

    @Test
    fun deletePictureDeletesPicture() {
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
        val res = categoryDao.loadAllPictures(requestCat).pictures

        MatcherAssert.assertThat(res, hasSize(numberOfPictures-1))
        MatcherAssert.assertThat(res, not(contains(deletionPicture)))
    }

    @Test
    fun deleteCategoryDeletesCategory() {
        val numberOfCategories = (1..10).random()
        val testCategories = mutableListOf<RoomCategory>()
        for (i in 0 until numberOfCategories) {
            testCategories.add(getRandomCategory())
        }

        categoryDao.insertAll(*testCategories.toTypedArray())

        val deletionCategory = testCategories.random()
        categoryDao.delete(deletionCategory)
        val res = categoryDao.loadAll()

        MatcherAssert.assertThat(res, CoreMatchers.not(Matchers.contains(deletionCategory)))
    }
}