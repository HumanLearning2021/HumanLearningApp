package com.github.HumanLearning2021.HumanLearningApp.firestore

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.HumanLearning2021.HumanLearningApp.hilt.*
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseService
import com.github.HumanLearning2021.HumanLearningApp.offline.OfflineCategorizedPicture
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
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

@UninstallModules(DatabaseServiceModule::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class CachedFirestoreDatabaseManagementTest {

    @Inject
    @Demo2Database
    lateinit var demo2DbService: DatabaseService

    @BindValue
    @Demo2Database
    lateinit var demo2DbMgt: DatabaseManagement

    @Inject
    @CachedDemoDatabase
    lateinit var demoInterface: DatabaseManagement

    @get:Rule
    val hiltRule = HiltAndroidRule(this)
    lateinit var appleCategoryId: String
    lateinit var pearCategoryId: String

    @Before
    fun setUp() {
        hiltRule.inject()
        demo2DbMgt = DatabaseManagementModule.provideDemo2Service(demo2DbService)
        appleCategoryId = "LbaIwsl1kizvTod4q1TG"
        pearCategoryId = "T4UkpkduhRtvjdCDqBFz"
    }

    @Test
    fun getPicturePutsItIntoCache() = runBlocking {
        val ids = demoInterface.getPictureIds(demoInterface.getCategoryById(appleCategoryId)!!)
        val pic = demoInterface.getPicture(ids.random())
        assert(pic is OfflineCategorizedPicture)
        assertThat(pic, not(equalTo(null)))
        assertThat((demoInterface as CachedFirestoreDatabaseManagement).cachedPictures.keys, hasItem(pic!!.id))
    }

    @Test
    fun removePictureRemovesItFromCache() = runBlocking {
        val ids = demoInterface.getPictureIds(demoInterface.getCategoryById(appleCategoryId)!!)
        val pic = demoInterface.getPicture(ids.random())
        assumeThat(pic, not(equalTo(null)))
        assumeThat((demoInterface as CachedFirestoreDatabaseManagement).cachedPictures.keys, hasItem(pic!!.id))
        demoInterface.removePicture(pic)
        assertThat((demoInterface as CachedFirestoreDatabaseManagement).cachedPictures.keys, not(hasItem(pic.id)))
    }

    @Test
    fun getPictureWorksFromCache() = runBlocking {
        val ids = demoInterface.getPictureIds(demoInterface.getCategoryById(appleCategoryId)!!)
        val pic = demoInterface.getPicture(ids.random())
        assumeThat(pic, not(equalTo(null)))
        assumeThat((demoInterface as CachedFirestoreDatabaseManagement).cachedPictures.keys, hasItem(pic!!.id))
        val pic2 = demoInterface.getPicture(pic.id)
        assertThat(pic2, not(equalTo(null)))
    }

    @Test
    fun getReprPicturePutsItIntoCache() = runBlocking {
        val pic = demoInterface.getRepresentativePicture("pear")
        assumeThat(pic, not(equalTo(null)))
        assertThat((demoInterface as CachedFirestoreDatabaseManagement).cachedPictures.keys, hasItem(pic!!.id))
    }

    @Test
    fun getPictureDeprecatedPutsItIntoCache() = runBlocking {
        val pic = demoInterface.getPicture(demoInterface.getCategoryById(appleCategoryId)!!)
        assumeThat(pic, not(equalTo(null)))
        assumeThat((demoInterface as CachedFirestoreDatabaseManagement).cachedPictures.keys, hasItem(pic!!.id))
        demoInterface.removePicture(pic)
        assertThat((demoInterface as CachedFirestoreDatabaseManagement).cachedPictures.keys, not(hasItem(pic.id)))

    }
}