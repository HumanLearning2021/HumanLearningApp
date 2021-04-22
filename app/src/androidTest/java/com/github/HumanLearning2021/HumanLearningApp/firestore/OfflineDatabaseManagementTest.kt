package com.github.HumanLearning2021.HumanLearningApp.firestore

import android.content.Context
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.hilt.OfflineDemoDatabase
import com.github.HumanLearning2021.HumanLearningApp.hilt.OfflineScratchDatabase
import com.github.HumanLearning2021.HumanLearningApp.model.*
import com.github.HumanLearning2021.HumanLearningApp.offline.OfflineCategory
import com.github.HumanLearning2021.HumanLearningApp.offline.OfflineDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.offline.OfflineDataset
import com.github.HumanLearning2021.HumanLearningApp.offline.PictureRepository
import com.github.HumanLearning2021.HumanLearningApp.room.RoomOfflineDatabase
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.equalTo
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

    @Inject
    lateinit var room: RoomOfflineDatabase

    @Inject
    @ApplicationContext
    lateinit var context: Context

    @Inject
    @OfflineDemoDatabase
    lateinit var demoManagement: DatabaseManagement

    @get:Rule
    val hiltRule = HiltAndroidRule(this)
    lateinit var appleCategoryId: String
    lateinit var pearCategoryId: String
    lateinit var fakeCategory: FirestoreCategory
    lateinit var fakeDataset: FirestoreDataset

    @Before
    fun setUp() = runBlocking {
        hiltRule.inject()
        appleCategoryId = "LbaIwsl1kizvTod4q1TG"
        pearCategoryId = "T4UkpkduhRtvjdCDqBFz"
        fakeCategory =  FirestoreCategory("oopsy/oopsy", "oopsy", "oopsy")
        fakeDataset = FirestoreDataset("oopsy/oopsy", "oopsy", "oopsy", setOf())
    }

    @After
    fun teardown() {
        room.clearAllTables()
        PictureRepository("demo", context).clear()
    }

    private fun getRandomString() = "${UUID.randomUUID()}"

    @Test
    fun test_getPicture_categoryNotPresent() = runBlocking {
        runCatching {
            demoManagement.getPicture(FirestoreCategory("path", getRandomString(), getRandomString()))
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
        MatcherAssert.assertThat(pic!!.category.id, Matchers.equalTo(appleCategory.id))
    }

    @Test
    fun test_getPictureIds() = runBlocking {
        val ids = demoManagement.getPictureIds(demoManagement.getCategoryById(appleCategoryId)!!)
        MatcherAssert.assertThat(ids, Matchers.hasSize(5))
        MatcherAssert.assertThat(ids, Matchers.containsInAnyOrder("apple01", "apple02", "apple03", "weird_apple", "minecraft_apple"))
    }

    @Test
    fun test_getPictureById() = runBlocking {
        val appleCategory = demoManagement.getCategoryById(appleCategoryId) as OfflineCategory
        requireNotNull(appleCategory, { "category of apples no found in demo database" })
        val picId = demoManagement.getPictureIds(appleCategory).random()
        val pic = demoManagement.getPicture(picId)
        MatcherAssert.assertThat(pic!!.category.id, Matchers.equalTo(appleCategory.id))
    }

    @Test
    fun test_getRepresentativePicture_null() = runBlocking {
        MatcherAssert.assertThat(demoManagement.getRepresentativePicture(pearCategoryId), Matchers.equalTo(null))
    }

    @Test
    fun test_getRepresentativePicture() = runBlocking {
        val appleCategory = demoManagement.getCategoryById(appleCategoryId)
        MatcherAssert.assertThat(
            demoManagement.getRepresentativePicture(appleCategoryId)!!.category.id, equalTo(appleCategory!!.id)
        )
    }

    @Test
    fun test_putPicture_categoryNotPresent() = runBlocking {
        runCatching {
            val tmp = File.createTempFile("meow", ".png")
            val uri = Uri.fromFile(tmp)
            demoManagement.putPicture(uri, FirestoreCategory("path", getRandomString(), getRandomString()))
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
        val cat = demoManagement.putCategory(name)

        val tmp = File.createTempFile("meow", ".png")
        val pic = try {
            ctx.resources.openRawResource(R.drawable.fork).use { img ->
                tmp.outputStream().use {
                    img.copyTo(it)
                }
            }
            val uri = Uri.fromFile(tmp)
            demoManagement.putPicture(uri, cat)
        } finally {
            tmp.delete()
        }
        MatcherAssert.assertThat(pic.category.id, equalTo(cat.id))
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
        MatcherAssert.assertThat(demoManagement.putCategory(name), hasName(name))
    }

    @Test
    fun test_getCategories() = runBlocking {
        val cats = demoManagement.getCategories()
        MatcherAssert.assertThat(cats, Matchers.hasItems(hasName("Pomme")))
    }

    @Test
    fun test_getAllPictures_categoryNotPresent() = runBlocking {
        runCatching {
            demoManagement.getAllPictures(fakeCategory)
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
        MatcherAssert.assertThat(pics.map { p -> p.category.id }, Matchers.hasItems(Matchers.equalTo(cat.id)))
    }

    @Test
    fun test_removeCategory() = runBlocking {
        val cat = demoManagement.putCategory(getRandomString())
        requireNotNull(demoManagement.getCategoryById(cat.id), {"category was not put into database"})
        demoManagement.removeCategory(cat)
        MatcherAssert.assertThat(demoManagement.getCategoryById(cat.id), Matchers.equalTo(null))
    }

    @Test
    fun test_removePicture() = runBlocking {
        val name = getRandomString()
        val ctx = ApplicationProvider.getApplicationContext<Context>()
        val cat = demoManagement.putCategory(name)

        val tmp = File.createTempFile("meow", ".png")
        val pic = try {
            ctx.resources.openRawResource(R.drawable.fork).use { img ->
                tmp.outputStream().use {
                    img.copyTo(it)
                }
            }
            val uri = Uri.fromFile(tmp)
            demoManagement.putPicture(uri, cat)
        } finally {
            tmp.delete()
        }
        demoManagement.removePicture(pic)
        MatcherAssert.assertThat(demoManagement.getPicture(pic.id), Matchers.equalTo(null))
    }

    @Test
    fun test_getPictureIds_throwsIfCategoryNotPresent() = runBlocking {
        runCatching {
            demoManagement.getAllPictures(fakeCategory)
        }.fold({
            Assert.fail("unexpected successful completion")
        }, {
            MatcherAssert.assertThat(it, Matchers.instanceOf(IllegalArgumentException::class.java))
        })
    }

    @Test
    fun test_putDataset() = runBlocking {
        val name = getRandomString()
        val ds = demoManagement.putDataset(name, setOf())
        MatcherAssert.assertThat(demoManagement.getDatasetById(ds.id)!!.name, Matchers.equalTo(name))
    }

    @Test
    fun test_getDatasetById() = runBlocking {
        MatcherAssert.assertThat(
            demoManagement.getDatasetById("PzuR0B48GpYN5ERxM3DW"), Matchers.not(
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
        val ds = demoManagement.putDataset(getRandomString(), setOf())
        requireNotNull(demoManagement.getDatasetById(ds.id))
        demoManagement.deleteDataset(ds.id)
        MatcherAssert.assertThat(demoManagement.getDatasetById(ds.id), Matchers.equalTo(null))
    }

    @Test
    fun test_putRepresentativePicture_categoryNotPresent() = runBlocking {
        runCatching {
            demoManagement.putRepresentativePicture(Uri.EMPTY, fakeCategory)
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
        val cat = demoManagement.putCategory(randomCategoryName)
        val tmp = File.createTempFile("droid", ".png")
        try {
            ctx.resources.openRawResource(R.drawable.fork).use { img ->
                tmp.outputStream().use {
                    img.copyTo(it)
                }
            }
            val uri = Uri.fromFile(tmp)
            demoManagement.putRepresentativePicture(uri, cat)
        } finally {
            tmp.delete()
        }

        MatcherAssert.assertThat(
            demoManagement.getRepresentativePicture(cat.id), Matchers.not(
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
        val cat1 = demoManagement.putCategory(getRandomString())
        val cat2 = demoManagement.putCategory(getRandomString())
        val fakeDs = OfflineDataset(getRandomString(), getRandomString(), setOf(cat1, cat2))
        val res = demoManagement.removeCategoryFromDataset(fakeDs, cat2)
        MatcherAssert.assertThat(res.categories, Matchers.equalTo(setOf(cat1)))
    }

    @Test
    fun test_removeCategoryFromDataset_categoryNotPresent() = runBlocking {
        val cat1 = demoManagement.putCategory(getRandomString())
        val cat2 = Converters.fromCategory(fakeCategory)
        val fakeDs = OfflineDataset(getRandomString(), getRandomString(), setOf(cat1, cat2))
        val res = demoManagement.removeCategoryFromDataset(fakeDs, cat2)
        MatcherAssert.assertThat(res.categories, Matchers.equalTo(setOf(cat1)))
    }

    @Test
    fun test_removeCategoryFromDataset() = runBlocking {
        val cat1 = demoManagement.putCategory(getRandomString())
        val cat2 = demoManagement.putCategory(getRandomString())
        val ds = demoManagement.putDataset(getRandomString(), setOf(cat1, cat2))
        demoManagement.removeCategoryFromDataset(ds, cat2)
        val cats = demoManagement.getDatasetById(ds.id)!!.categories
        MatcherAssert.assertThat(cats.size, Matchers.equalTo(1))
        val catIds = cats.map { c -> c.id }
        assert(catIds.contains(cat1.id))
        assert(!catIds.contains(cat2.id))
    }

    @Test
    fun test_editDatasetName_datasetNotPresent() = runBlocking {
        val fakeDs = FirestoreDataset("path", getRandomString(), getRandomString(), setOf())
        runCatching {
            demoManagement.editDatasetName(fakeDs, getRandomString())
        }.fold({
            Assert.fail("unexpected successful completion")
        }, {
            MatcherAssert.assertThat(it, Matchers.instanceOf(IllegalArgumentException::class.java))
        })
    }

    @Test
    fun test_editDatasetName() = runBlocking {
        val ogName = getRandomString()
        val ds = demoManagement.putDataset(ogName, setOf())
        val newName = getRandomString()
        MatcherAssert.assertThat(demoManagement.getDatasetById(ds.id)!!.name, Matchers.equalTo(ogName))
        demoManagement.editDatasetName(ds, newName)
        MatcherAssert.assertThat(demoManagement.getDatasetById(ds.id)!!.name, Matchers.equalTo(newName))
    }

    @Test
    fun test_addCategoryToDataset_categoryNotPresent() = runBlocking {
        val fakeDs = FirestoreDataset("path", getRandomString(), getRandomString(), setOf())
        runCatching {
            demoManagement.addCategoryToDataset(fakeDs, fakeCategory)
        }.fold({
            Assert.fail("unexpected successful completion")
        }, {
            MatcherAssert.assertThat(it, Matchers.instanceOf(IllegalArgumentException::class.java))
        })
    }
}