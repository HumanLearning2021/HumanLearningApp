package com.github.HumanLearning2021.HumanLearningApp.offline

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.HumanLearning2021.HumanLearningApp.hilt.CachedDemoDatabase
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseService
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.hasItem
import org.junit.Assume.assumeThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class CachedDatabaseServiceTest {

    @Inject
    @ApplicationContext
    lateinit var context: Context

    @Inject
    @CachedDemoDatabase
    lateinit var demoInterface: DatabaseService

    @get:Rule
    val hiltRule = HiltAndroidRule(this)
    lateinit var appleCategoryId: String
    lateinit var pearCategoryId: String

    @Before
    fun setUp() {
        hiltRule.inject()
        appleCategoryId = "LbaIwsl1kizvTod4q1TG"
        pearCategoryId = "T4UkpkduhRtvjdCDqBFz"
    }

    @Test
    fun getPicturePutsItIntoCache() = runBlocking {
        val ids = demoInterface.getPictureIds(demoInterface.getCategory(appleCategoryId)!!)
        val pic = demoInterface.getPicture(ids.random())
        assert(pic is OfflineCategorizedPicture)
        assertThat(pic, not(equalTo(null)))
        assertThat((demoInterface as CachedDatabaseService).cachedPictures.keys, hasItem(pic!!.id))
    }

    @Test
    fun removePictureRemovesItFromCache() = runBlocking {
        val ids = demoInterface.getPictureIds(demoInterface.getCategory(appleCategoryId)!!)
        val pic = demoInterface.getPicture(ids.random())
        assumeThat(pic, not(equalTo(null)))
        assumeThat((demoInterface as CachedDatabaseService).cachedPictures.keys, hasItem(pic!!.id))
        demoInterface.removePicture(pic)
        assertThat(
            (demoInterface as CachedDatabaseService).cachedPictures.keys,
            not(hasItem(pic.id))
        )
    }

    @Test
    fun getPictureWorksFromCache() = runBlocking {
        val ids = demoInterface.getPictureIds(demoInterface.getCategory(appleCategoryId)!!)
        val pic = demoInterface.getPicture(ids.random())
        assumeThat(pic, not(equalTo(null)))
        assumeThat((demoInterface as CachedDatabaseService).cachedPictures.keys, hasItem(pic!!.id))
        val pic2 = demoInterface.getPicture(pic.id)
        assertThat(pic2, not(equalTo(null)))
    }

    @Test
    fun getRepresentativePicturePutsItIntoCache() = runBlocking {
        val pic = demoInterface.getRepresentativePicture(appleCategoryId)
        assumeThat(pic, not(equalTo(null)))
        assertThat((demoInterface as CachedDatabaseService).cachedPictures.keys, hasItem(pic!!.id))
    }

    @Test
    fun getRepresentativePictureWorks() = runBlocking {
        val pic = demoInterface.getRepresentativePicture(appleCategoryId)
        assumeThat(pic, not(equalTo(null)))
        assumeThat((demoInterface as CachedDatabaseService).cachedPictures.keys, hasItem(pic!!.id))
        val pic2 = demoInterface.getPicture(pic.id)
        assertThat(pic2, not(equalTo(null)))
    }

    @Test
    fun retrieveRepresentativePictureTwiceWorks() = runBlocking {
        val pic1 = demoInterface.getRepresentativePicture(appleCategoryId)
        assumeThat(pic1, not(equalTo(null)))
        val pic2 = demoInterface.getRepresentativePicture(appleCategoryId)
        assertThat(pic2!!.id, equalTo(pic1!!.id))
    }

    @Test
    fun getPictureTwiceWorksWithCacheClearedInBetween() = runBlocking {
        val id = demoInterface.getPictureIds(demoInterface.getCategory(appleCategoryId)!!).random()
        val pic1 = demoInterface.getPicture(id)
        assumeThat(pic1, not(equalTo(null)))
        assumeThat(
            context.cacheDir.listFiles()?.toList()?.forEach { file -> file.delete() }, not(
                equalTo(null)
            )
        )
        val pic2 = demoInterface.getPicture(id)
        assumeThat(pic2, not(equalTo(null)))
        assertThat(pic1!!.id, equalTo(pic2!!.id))
    }
}