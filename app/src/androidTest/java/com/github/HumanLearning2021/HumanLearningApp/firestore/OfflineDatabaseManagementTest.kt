package com.github.HumanLearning2021.HumanLearningApp.firestore

import android.content.Context
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.hilt.DatabaseManagementModule
import com.github.HumanLearning2021.HumanLearningApp.hilt.OfflineDemoDatabase
import com.github.HumanLearning2021.HumanLearningApp.hilt.OfflineScratchDatabase
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.UniqueDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.hasCategory
import com.github.HumanLearning2021.HumanLearningApp.model.hasName
import com.github.HumanLearning2021.HumanLearningApp.room.RoomEmptyHLDatabase
import com.github.HumanLearning2021.HumanLearningApp.room.RoomOfflineDatabase
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.*
import org.junit.runner.RunWith
import java.io.File
import java.lang.IllegalArgumentException
import java.util.*
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class OfflineDatabaseManagementTest {

    private fun dlDemo() = runBlocking {
        UniqueDatabaseManagement().downloadDatabase("demo")
    }

    @Inject
    @OfflineDemoDatabase
    lateinit var demoManagement: DatabaseManagement

    @Inject
    @OfflineScratchDatabase
    lateinit var scratchManagement: DatabaseManagement

    @get:Rule
    val hiltRule = HiltAndroidRule(this)
    lateinit var appleCategoryId: String
    lateinit var pearCategoryId: String
    lateinit var fakeCategory: FirestoreCategory
    lateinit var fakeDataset: FirestoreDataset

    @Before
    fun setUp() {
        dlDemo()
        RoomOfflineDatabase.getDatabase(ApplicationProvider.getApplicationContext()).databaseDao().insertAll(
            RoomEmptyHLDatabase("offlineScratch")
        )
        Thread.sleep(5000)
        hiltRule.inject()
        appleCategoryId = "LbaIwsl1kizvTod4q1TG"
        pearCategoryId = "T4UkpkduhRtvjdCDqBFz"
        fakeCategory =  FirestoreCategory("oopsy/oopsy", "oopsy", "oopsy")
        fakeDataset = FirestoreDataset("oopsy/oopsy", "oopsy", "oopsy", setOf())
    }

    @After
    fun teardown() {
        RoomOfflineDatabase.getDatabase(ApplicationProvider.getApplicationContext()).clearAllTables()
        Thread.sleep(1000) //wait for above method to complete
    }

    private fun getRandomString() = "${UUID.randomUUID()}"

    @Test
    fun test_getPicture_categoryNotPresent() = runBlocking {
        runCatching {
            scratchManagement.getPicture(FirestoreCategory("path", getRandomString(), getRandomString()))
        }.fold({
            Assert.fail("unexpected successful completion")
        }, {
            MatcherAssert.assertThat(it, Matchers.instanceOf(IllegalArgumentException::class.java))
        })
    }

    @Test
    fun test_getPicture() = runBlocking {
        val appleCategory = demoManagement.getCategoryById(appleCategoryId)
        requireNotNull(appleCategory, { "category of apples no found in demo database" })
        val pic = demoManagement.getPicture(appleCategory)
        MatcherAssert.assertThat(pic, hasCategory(Matchers.equalTo(appleCategory)))
    }

    @Test
    fun test_getRepresentativePicture_null() = runBlocking {
        MatcherAssert.assertThat(demoManagement.getRepresentativePicture(pearCategoryId), Matchers.equalTo(null))
    }

    @Test
    fun test_getRepresentativePicture() = runBlocking {
        val appleCategory = demoManagement.getCategoryById(appleCategoryId)
        MatcherAssert.assertThat(demoManagement.getRepresentativePicture(appleCategoryId), hasCategory(
            Matchers.equalTo(appleCategory)
        )
        )
    }

    @Test
    fun test_putPicture_categoryNotPresent() = runBlocking {
        runCatching {
            val tmp = File.createTempFile("meow", ".png")
            val uri = Uri.fromFile(tmp)
            scratchManagement.putPicture(uri, FirestoreCategory("path", getRandomString(), getRandomString()))
            tmp.delete()
        }.fold({
            Assert.fail("unexpected successful completion")
        }, {
            MatcherAssert.assertThat(it, Matchers.instanceOf(IllegalArgumentException::class.java))
        })
    }

    @Test
    fun test_putPicture() = runBlocking {
        val name = getRandomString()
        val ctx = ApplicationProvider.getApplicationContext<Context>()
        val cat = scratchManagement.putCategory(name)

        val tmp = File.createTempFile("meow", ".png")
        val pic = try {
            ctx.resources.openRawResource(R.drawable.fork).use { img ->
                tmp.outputStream().use {
                    img.copyTo(it)
                }
            }
            val uri = Uri.fromFile(tmp)
            scratchManagement.putPicture(uri, cat)
        } finally {
            tmp.delete()
        }
        MatcherAssert.assertThat(pic, hasCategory(Matchers.equalTo(cat)))
    }

    @Test
    fun test_getCategoryById_null() = runBlocking {
        MatcherAssert.assertThat(demoManagement.getCategoryById(getRandomString()), Matchers.equalTo(null))
    }

    @Test
    fun test_getCategoryById() = runBlocking {
        MatcherAssert.assertThat(demoManagement.getCategoryById(appleCategoryId), hasName("Pomme"))
    }

    @Test
    fun test_getCategoryByName() = runBlocking {
        MatcherAssert.assertThat(demoManagement.getCategoryByName("Poire").first(), hasName("Poire"))
    }

    @Test
    fun test_putCategory() = runBlocking {
        val name = getRandomString()
        MatcherAssert.assertThat(scratchManagement.putCategory(name), hasName(name))
    }

    @Test
    fun test_getCategories() = runBlocking {
        val cats = demoManagement.getCategories()
        MatcherAssert.assertThat(cats, Matchers.hasItems(hasName("Pomme")))
    }

    @Test
    fun test_getAllPictures_categoryNotPresent() = runBlocking {
        runCatching {
            scratchManagement.getAllPictures(fakeCategory)
        }.fold({
            Assert.fail("unexpected successful completion")
        }, {
            MatcherAssert.assertThat(it, Matchers.instanceOf(IllegalArgumentException::class.java))
        })
    }

    @Test
    fun test_getAllPictures() = runBlocking {
        val cat = demoManagement.getCategoryById(appleCategoryId)
        val pics = demoManagement.getAllPictures(cat!!)
        MatcherAssert.assertThat(pics, Matchers.hasSize(5))
        MatcherAssert.assertThat(pics, Matchers.hasItems(hasCategory(Matchers.equalTo(cat))))
    }

    @Test
    fun test_removeCategory() = runBlocking {
        val cat = scratchManagement.putCategory(getRandomString())
        requireNotNull(scratchManagement.getCategoryById(cat.id), {"category was not put into database"})
        scratchManagement.removeCategory(cat)
        MatcherAssert.assertThat(scratchManagement.getCategoryById(cat.id), Matchers.equalTo(null))
    }

    @Test
    fun test_removePicture() = runBlocking {
        val name = getRandomString()
        val ctx = ApplicationProvider.getApplicationContext<Context>()
        val cat = scratchManagement.putCategory(name)

        val tmp = File.createTempFile("meow", ".png")
        val pic = try {
            ctx.resources.openRawResource(R.drawable.fork).use { img ->
                tmp.outputStream().use {
                    img.copyTo(it)
                }
            }
            val uri = Uri.fromFile(tmp)
            scratchManagement.putPicture(uri, cat)
        } finally {
            tmp.delete()
        }
        scratchManagement.removePicture(pic)
        MatcherAssert.assertThat(scratchManagement.getPicture(cat), Matchers.equalTo(null))
    }

    @Test
    fun test_getPictureIds_throwsIfCategoryNotPresent() = runBlocking {
        runCatching {
            scratchManagement.getAllPictures(fakeCategory)
        }.fold({
            Assert.fail("unexpected successful completion")
        }, {
            MatcherAssert.assertThat(it, Matchers.instanceOf(IllegalArgumentException::class.java))
        })
    }

    @Test
    fun test_getPictureIds() = runBlocking {
        val ids = demoManagement.getPictureIds(demoManagement.getCategoryById(appleCategoryId)!!)
        MatcherAssert.assertThat(ids, Matchers.hasSize(5))
        MatcherAssert.assertThat(ids, Matchers.containsInAnyOrder("apple01", "apple02", "apple03", "weird_apple", "minecraft_apple"))
    }

    @Test
    fun test_getPictureById() = runBlocking {
        val pic = demoManagement.getPicture("apple01")!!
        MatcherAssert.assertThat(pic.category, Matchers.equalTo(demoManagement.getCategoryById(appleCategoryId)))
    }

    @Test
    fun test_putDataset() = runBlocking {
        val name = getRandomString()
        val ds = scratchManagement.putDataset(name, setOf())
        MatcherAssert.assertThat(scratchManagement.getDatasetById(ds.id)!!.name, Matchers.equalTo(name))
    }

    @Test
    fun test_getDatasetById() = runBlocking {
        MatcherAssert.assertThat(demoManagement.getDatasetById("PzuR0B48GpYN5ERxM3DW"), Matchers.not(
            Matchers.equalTo(null)
        )
        )
    }

    @Test
    fun test_getDatasetByName() = runBlocking {
        MatcherAssert.assertThat(demoManagement.getDatasetByName("Fruit").size, Matchers.equalTo(1))
    }

    @Test
    fun test_deleteDataset() = runBlocking {
        val ds = scratchManagement.putDataset(getRandomString(), setOf())
        requireNotNull(scratchManagement.getDatasetById(ds.id))
        scratchManagement.deleteDataset(ds.id)
        MatcherAssert.assertThat(scratchManagement.getDatasetById(ds.id), Matchers.equalTo(null))
    }

    @Test
    fun test_putRepresentativePicture_categoryNotPresent() = runBlocking {
        runCatching {
            scratchManagement.putRepresentativePicture(Uri.EMPTY, fakeCategory)
        }.fold({
            Assert.fail("unexpected successful completion")
        }, {
            MatcherAssert.assertThat(it, Matchers.instanceOf(IllegalArgumentException::class.java))
        })
    }

    @Test
    fun test_putRepresentativePicture() = runBlocking {
        val randomCategoryName = getRandomString()
        val ctx = ApplicationProvider.getApplicationContext<Context>()
        val cat = scratchManagement.putCategory(randomCategoryName)
        val tmp = File.createTempFile("droid", ".png")
        try {
            ctx.resources.openRawResource(R.drawable.fork).use { img ->
                tmp.outputStream().use {
                    img.copyTo(it)
                }
            }
            val uri = Uri.fromFile(tmp)
            scratchManagement.putRepresentativePicture(uri, cat)
        } finally {
            tmp.delete()
        }

        MatcherAssert.assertThat(scratchManagement.getRepresentativePicture(cat.id), Matchers.not(
            Matchers.equalTo(null)
        )
        )
    }

    @Test
    fun test_getDatasets() = runBlocking {
        MatcherAssert.assertThat(demoManagement.getDatasets().size, Matchers.equalTo(1))
        MatcherAssert.assertThat(demoManagement.getDatasets().first().name, Matchers.equalTo("Fruit"))
    }

    @Test
    fun test_getDatasetNames() = runBlocking {
        MatcherAssert.assertThat(demoManagement.getDatasetNames().size, Matchers.equalTo(1))
        MatcherAssert.assertThat(demoManagement.getDatasetNames().first(), Matchers.equalTo("Fruit"))
    }

    @Test
    fun test_getDatasetIds() = runBlocking {
        MatcherAssert.assertThat(demoManagement.getDatasetIds().size, Matchers.equalTo(1))
        MatcherAssert.assertThat(demoManagement.getDatasetIds().first(), Matchers.equalTo("PzuR0B48GpYN5ERxM3DW"))
    }

    @Test
    fun test_removeCategoryFromDataset_datasetNotPresent() = runBlocking {
        val cat1 = scratchManagement.putCategory(getRandomString()) as FirestoreCategory
        val cat2 = scratchManagement.putCategory(getRandomString()) as FirestoreCategory
        val fakeDs = FirestoreDataset("path", getRandomString(), getRandomString(), setOf(cat1, cat2))
        val res = scratchManagement.removeCategoryFromDataset(fakeDs, cat2)
        MatcherAssert.assertThat(res.categories, Matchers.equalTo(setOf(cat1)))
    }

    @Test
    fun test_removeCategoryFromDataset_categoryNotPresent() = runBlocking {
        val cat1 = scratchManagement.putCategory(getRandomString()) as FirestoreCategory
        val cat2 = fakeCategory
        val fakeDs = FirestoreDataset("path", getRandomString(), getRandomString(), setOf(cat1, cat2))
        val res = scratchManagement.removeCategoryFromDataset(fakeDs, cat2)
        MatcherAssert.assertThat(res.categories, Matchers.equalTo(setOf(cat1)))
    }

    @Test
    fun test_removeCategoryFromDataset() = runBlocking {
        val cat1 = scratchManagement.putCategory(getRandomString())
        val cat2 = scratchManagement.putCategory(getRandomString())
        val ds = scratchManagement.putDataset(getRandomString(), setOf(cat1, cat2))
        scratchManagement.removeCategoryFromDataset(ds, cat2)
        val cats = scratchManagement.getDatasetById(ds.id)!!.categories
        MatcherAssert.assertThat(cats.size, Matchers.equalTo(1))
        assert(cats.contains(cat1))
        assert(!cats.contains(cat2))
    }

    @Test
    fun test_editDatasetName_datasetNotPresent() = runBlocking {
        val fakeDs = FirestoreDataset("path", getRandomString(), getRandomString(), setOf())
        runCatching {
            scratchManagement.editDatasetName(fakeDs, getRandomString())
        }.fold({
            Assert.fail("unexpected successful completion")
        }, {
            MatcherAssert.assertThat(it, Matchers.instanceOf(IllegalArgumentException::class.java))
        })
    }

    @Test
    fun test_editDatasetName() = runBlocking {
        val ogName = getRandomString()
        val ds = scratchManagement.putDataset(ogName, setOf())
        val newName = getRandomString()
        MatcherAssert.assertThat(scratchManagement.getDatasetById(ds.id)!!.name, Matchers.equalTo(ogName))
        scratchManagement.editDatasetName(ds, newName)
        MatcherAssert.assertThat(scratchManagement.getDatasetById(ds.id)!!.name, Matchers.equalTo(newName))
    }

    @Test
    fun test_addCategoryToDataset_categoryNotPresent() = runBlocking {
        val fakeDs = FirestoreDataset("path", getRandomString(), getRandomString(), setOf())
        runCatching {
            scratchManagement.addCategoryToDataset(fakeDs, fakeCategory)
        }.fold({
            Assert.fail("unexpected successful completion")
        }, {
            MatcherAssert.assertThat(it, Matchers.instanceOf(IllegalArgumentException::class.java))
        })
    }
}