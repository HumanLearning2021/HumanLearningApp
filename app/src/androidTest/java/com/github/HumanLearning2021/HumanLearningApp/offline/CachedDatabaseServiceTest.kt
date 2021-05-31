package com.github.HumanLearning2021.HumanLearningApp.offline

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.HumanLearning2021.HumanLearningApp.firestore.FirestoreDatabaseService
import com.google.firebase.firestore.FirebaseFirestore
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
    lateinit var firestore: FirebaseFirestore

    @Inject
    @ApplicationContext
    lateinit var context: Context

    private val dbName = "test"
    private lateinit var db: CachedDatabaseService

    @get:Rule
    val hiltRule = HiltAndroidRule(this)
    
    private val appleCategoryId = "LbaIwsl1kizvTod4q1TG"

    @Before
    fun setUp() {
        hiltRule.inject()
        db = CachedDatabaseService(
            FirestoreDatabaseService(dbName, firestore),
            PictureCache.applicationPictureCache(dbName, context),
        )
    }

    @Test
    fun getPicturePutsItIntoCache() = runBlocking {
        val ids = db.getPictureIds(db.getCategory(appleCategoryId)!!)
        val pic = db.getPicture(ids.random())
        assertThat(pic, not(equalTo(null)))
        assertThat(db.cachedPictures.keys, hasItem(pic!!.id))
    }

    @Test
    fun removePictureRemovesItFromCache() = runBlocking {
        val ids = db.getPictureIds(db.getCategory(appleCategoryId)!!)
        val pic = db.getPicture(ids.random())
        assumeThat(pic, not(equalTo(null)))
        assumeThat(db.cachedPictures.keys, hasItem(pic!!.id))
        db.removePicture(pic)
        assertThat(
            db.cachedPictures.keys,
            not(hasItem(pic.id))
        )
    }

    @Test
    fun getPictureWorksFromCache() = runBlocking {
        val ids = db.getPictureIds(db.getCategory(appleCategoryId)!!)
        val pic = db.getPicture(ids.random())
        assumeThat(pic, not(equalTo(null)))
        assumeThat(db.cachedPictures.keys, hasItem(pic!!.id))
        val pic2 = db.getPicture(pic.id)
        assertThat(pic2, not(equalTo(null)))
    }

    @Test
    fun getRepresentativePicturePutsItIntoCache() = runBlocking {
        val pic = db.getRepresentativePicture(appleCategoryId)
        assumeThat(pic, not(equalTo(null)))
        assertThat(db.cachedPictures.keys, hasItem(pic!!.id))
    }

    @Test
    fun getRepresentativePictureWorks() = runBlocking {
        val pic = db.getRepresentativePicture(appleCategoryId)
        assumeThat(pic, not(equalTo(null)))
        assumeThat(db.cachedPictures.keys, hasItem(pic!!.id))
        val pic2 = db.getPicture(pic.id)
        assertThat(pic2, not(equalTo(null)))
    }

    @Test
    fun retrieveRepresentativePictureTwiceWorks() = runBlocking {
        val pic1 = db.getRepresentativePicture(appleCategoryId)
        assumeThat(pic1, not(equalTo(null)))
        val pic2 = db.getRepresentativePicture(appleCategoryId)
        assertThat(pic2!!.id, equalTo(pic1!!.id))
    }

    @Test
    fun getPictureTwiceWorksWithCacheClearedInBetween() = runBlocking {
        val id = db.getPictureIds(db.getCategory(appleCategoryId)!!).random()
        val pic1 = db.getPicture(id)
        assumeThat(pic1, not(equalTo(null)))
        assumeThat(
            context.cacheDir.listFiles()?.toList()?.forEach { file -> file.delete() }, not(
                equalTo(null)
            )
        )
        val pic2 = db.getPicture(id)
        assumeThat(pic2, not(equalTo(null)))
        assertThat(pic1!!.id, equalTo(pic2!!.id))
    }
}
