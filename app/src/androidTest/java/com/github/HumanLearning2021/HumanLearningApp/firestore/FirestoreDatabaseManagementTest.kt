package com.github.HumanLearning2021.HumanLearningApp.firestore

import android.content.Context
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.hilt.*
import com.github.HumanLearning2021.HumanLearningApp.hilt.DemoDatabase
import com.github.HumanLearning2021.HumanLearningApp.hilt.ScratchDatabase
import com.github.HumanLearning2021.HumanLearningApp.model.CategorizedPicture
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseService
import com.github.HumanLearning2021.HumanLearningApp.model.hasCategory
import com.github.HumanLearning2021.HumanLearningApp.model.hasName
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Assert.fail
import org.junit.Assume.assumeThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.lang.IllegalArgumentException
import java.util.*
import javax.inject.Inject

@UninstallModules(DatabaseServiceModule::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class FirestoreDatabaseManagementTest {

    @Inject
    @Demo2Database
    lateinit var demo2DbService: DatabaseService

    @BindValue
    @Demo2Database
    lateinit var demo2DbMgt: DatabaseManagement

    @Inject
    @DemoDatabase
    lateinit var demoManagement: DatabaseManagement

    @Inject
    @ScratchDatabase
    lateinit var scratchManagement: DatabaseManagement

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    lateinit var appleCategoryId: String
    lateinit var pearCategoryId: String
    lateinit var fakeCategory: FirestoreCategory
    lateinit var fakeDataset: FirestoreDataset

    @Before
    fun setUp() {
        hiltRule.inject()
        demo2DbMgt = DatabaseManagementModule.provideDemo2Service(demo2DbService)
        appleCategoryId = "LbaIwsl1kizvTod4q1TG"
        pearCategoryId = "T4UkpkduhRtvjdCDqBFz"
        fakeCategory =  FirestoreCategory("oopsy/oopsy", "oopsy", "oopsy")
        fakeDataset = FirestoreDataset("oopsy/oopsy", "oopsy", "oopsy", setOf())
    }

    private fun getRandomString() = "${UUID.randomUUID()}"

    @Test
    fun test_getPicture_categoryNotPresent() = runBlocking {
        runCatching {
            scratchManagement.getPicture(FirestoreCategory("path", getRandomString(), getRandomString()))
        }.fold({
            fail("unexpected successful completion")
        }, {
            assertThat(it, instanceOf(IllegalArgumentException::class.java))
        })
    }

    @Suppress("DEPRECATED")
    @Test
    fun test_getPicture() = runBlocking {
        val appleCategory = demoManagement.getCategoryById(appleCategoryId)
        requireNotNull(appleCategory, { "category of apples no found in demo database" })
        val pic = demoManagement.getPicture(appleCategory)
        assertThat(pic, hasCategory(equalTo(appleCategory)))
    }

    @Test
    fun test_getRepresentativePicture_null() = runBlocking {
        assertThat(demoManagement.getRepresentativePicture(pearCategoryId), equalTo(null))
    }

    @Test
    fun test_getRepresentativePicture() = runBlocking {
        val appleCategory = demoManagement.getCategoryById(appleCategoryId)
        assertThat(demoManagement.getRepresentativePicture(appleCategoryId), hasCategory(equalTo(appleCategory)))
    }

    @Test
    fun test_putPicture_categoryNotPresent() = runBlocking {
        runCatching {
            val tmp = File.createTempFile("meow", ".png")
            val uri = Uri.fromFile(tmp)
            scratchManagement.putPicture(uri, FirestoreCategory("path", getRandomString(), getRandomString()))
            tmp.delete()
        }.fold({
            fail("unexpected successful completion")
        }, {
            assertThat(it, instanceOf(IllegalArgumentException::class.java))
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
        assertThat(pic, hasCategory(equalTo(cat)))
    }

    @Test
    fun test_getCategoryById_null() = runBlocking {
        assertThat(demoManagement.getCategoryById(getRandomString()), equalTo(null))
    }

    @Test
    fun test_getCategoryById() = runBlocking {
        assertThat(demoManagement.getCategoryById(appleCategoryId), hasName("Pomme"))
    }

    @Test
    fun test_getCategoryByName() = runBlocking {
        assertThat(demoManagement.getCategoryByName("Poire").first(), hasName("Poire"))
    }

    @Test
    fun test_putCategory() = runBlocking {
        val name = getRandomString()
        assertThat(scratchManagement.putCategory(name), hasName(name))
    }

    @Test
    fun test_getCategories() = runBlocking {
        val cats = demoManagement.getCategories()
        assertThat(cats, hasItems(hasName("Pomme")))
    }

    @Test
    fun test_getAllPictures_categoryNotPresent() = runBlocking {
        runCatching {
            scratchManagement.getAllPictures(fakeCategory)
        }.fold({
            fail("unexpected successful completion")
        }, {
            assertThat(it, instanceOf(IllegalArgumentException::class.java))
        })
    }

    @Test
    fun test_getAllPictures() = runBlocking {
        val cat = demoManagement.getCategoryById(appleCategoryId)
        val pics = demoManagement.getAllPictures(cat!!)
        assertThat(pics, hasSize(5))
        assertThat(pics, hasItems(hasCategory(equalTo(cat))))
    }

    @Test
    fun test_removeCategory() = runBlocking {
        val cat = scratchManagement.putCategory(getRandomString())
        requireNotNull(scratchManagement.getCategoryById(cat.id), {"category was not put into database"})
        scratchManagement.removeCategory(cat)
        assertThat(scratchManagement.getCategoryById(cat.id), equalTo(null))
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
        assertThat(scratchManagement.getPicture(cat), equalTo(null))
    }

    @Test
    fun test_getPictureIds_throwsIfCategoryNotPresent() = runBlocking {
        runCatching {
            scratchManagement.getAllPictures(fakeCategory)
        }.fold({
            fail("unexpected successful completion")
        }, {
            assertThat(it, instanceOf(IllegalArgumentException::class.java))
        })
    }

    @Test
    fun test_getPictureIds() = runBlocking {
        val ids = demoManagement.getPictureIds(demoManagement.getCategoryById(appleCategoryId)!!)
        assertThat(ids, hasSize(5))
        assertThat(ids, containsInAnyOrder("apple01", "apple02", "apple03", "weird_apple", "minecraft_apple"))
    }

    @Test
    fun test_getPictureById() = runBlocking {
        val pic = demoManagement.getPicture("apple01")!!
        assertThat(pic.category, equalTo(demoManagement.getCategoryById(appleCategoryId)))
    }

    @Test
    fun test_putDataset() = runBlocking {
        val name = getRandomString()
        val ds = scratchManagement.putDataset(name, setOf())
        assertThat(scratchManagement.getDatasetById(ds.id)!!.name, equalTo(name))
    }

    @Test
    fun test_getDatasetById() = runBlocking {
        assertThat(demoManagement.getDatasetById("PzuR0B48GpYN5ERxM3DW"), not(equalTo(null)))
    }

    @Test
    fun test_getDatasetByName() = runBlocking {
        assertThat(demoManagement.getDatasetByName("Fruit").size, equalTo(1))
    }

    @Test
    fun test_deleteDataset() = runBlocking {
        val ds = scratchManagement.putDataset(getRandomString(), setOf())
        requireNotNull(scratchManagement.getDatasetById(ds.id))
        scratchManagement.deleteDataset(ds.id)
        assertThat(scratchManagement.getDatasetById(ds.id), equalTo(null))
    }

    @Test
    fun test_putRepresentativePicture_categoryNotPresent() = runBlocking {
        runCatching {
            scratchManagement.putRepresentativePicture(Uri.EMPTY, fakeCategory)
        }.fold({
            fail("unexpected successful completion")
        }, {
            assertThat(it, instanceOf(IllegalArgumentException::class.java))
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
        assertThat(scratchManagement.getRepresentativePicture(cat.id), not(equalTo(null)))
    }

    @Test
    fun test_putRepresentativePicture_fromCategorizedPicture_pictureNotPresent() = runBlocking {
        runCatching {
            scratchManagement.putRepresentativePicture(FirestoreCategorizedPicture("${UUID.randomUUID()}", "some/path", fakeCategory, "url"))
        }.fold({
            fail("unexpected successful completion")
        }, {
            assertThat(it, instanceOf(IllegalArgumentException::class.java))
        })
    }

    @Test
    fun test_putRepresentativePicture_fromCategorizedPicture() = runBlocking {
        val randomCategoryName = getRandomString()
        val ctx = ApplicationProvider.getApplicationContext<Context>()
        val cat = scratchManagement.putCategory(randomCategoryName)
        val tmp = File.createTempFile("droid", ".png")
        var pic: CategorizedPicture
        try {
            ctx.resources.openRawResource(R.drawable.fork).use { img ->
                tmp.outputStream().use {
                    img.copyTo(it)
                }
            }
            val uri = Uri.fromFile(tmp)
            pic = scratchManagement.putPicture(uri, cat)
        } finally {
            tmp.delete()
        }
        assumeThat(scratchManagement.getPictureIds(pic.category), hasItem(pic.id))
        scratchManagement.putRepresentativePicture(pic)
        assertThat(scratchManagement.getRepresentativePicture(cat.id), not(equalTo(null)))
        assertThat(scratchManagement.getPictureIds(pic.category), not(hasItem(pic.id)))
    }

    @Test
    fun test_getDatasets() = runBlocking {
        assertThat(demoManagement.getDatasets().size, equalTo(1))
        assertThat(demoManagement.getDatasets().first().name, equalTo("Fruit"))
    }

    @Test
    fun test_getDatasetNames() = runBlocking {
        assertThat(demoManagement.getDatasetNames().size, equalTo(1))
        assertThat(demoManagement.getDatasetNames().first(), equalTo("Fruit"))
    }

    @Test
    fun test_getDatasetIds() = runBlocking {
        assertThat(demoManagement.getDatasetIds().size, equalTo(1))
        assertThat(demoManagement.getDatasetIds().first(), equalTo("PzuR0B48GpYN5ERxM3DW"))
    }

    @Test
    fun test_removeCategoryFromDataset_datasetNotPresent() = runBlocking {
        val cat1 = scratchManagement.putCategory(getRandomString()) as FirestoreCategory
        val cat2 = scratchManagement.putCategory(getRandomString()) as FirestoreCategory
        val fakeDs = FirestoreDataset("path", getRandomString(), getRandomString(), setOf(cat1, cat2))
        val res = scratchManagement.removeCategoryFromDataset(fakeDs, cat2)
        assertThat(res.categories, equalTo(setOf(cat1)))
    }

    @Test
    fun test_removeCategoryFromDataset_categoryNotPresent() = runBlocking {
        val cat1 = scratchManagement.putCategory(getRandomString()) as FirestoreCategory
        val cat2 = fakeCategory
        val fakeDs = FirestoreDataset("path", getRandomString(), getRandomString(), setOf(cat1, cat2))
        val res = scratchManagement.removeCategoryFromDataset(fakeDs, cat2)
        assertThat(res.categories, equalTo(setOf(cat1)))
    }

    @Test
    fun test_removeCategoryFromDataset() = runBlocking {
        val cat1 = scratchManagement.putCategory(getRandomString())
        val cat2 = scratchManagement.putCategory(getRandomString())
        val ds = scratchManagement.putDataset(getRandomString(), setOf(cat1, cat2))
        scratchManagement.removeCategoryFromDataset(ds, cat2)
        val cats = scratchManagement.getDatasetById(ds.id)!!.categories
        assertThat(cats.size, equalTo(1))
        assert(cats.contains(cat1))
        assert(!cats.contains(cat2))
    }

    @Test
    fun test_editDatasetName_datasetNotPresent() = runBlocking {
        val fakeDs = FirestoreDataset("path", getRandomString(), getRandomString(), setOf())
        runCatching {
            scratchManagement.editDatasetName(fakeDs, getRandomString())
        }.fold({
            fail("unexpected successful completion")
        }, {
            assertThat(it, instanceOf(IllegalArgumentException::class.java))
        })
    }

    @Test
    fun test_editDatasetName() = runBlocking {
        val ogName = getRandomString()
        val ds = scratchManagement.putDataset(ogName, setOf())
        val newName = getRandomString()
        assertThat(scratchManagement.getDatasetById(ds.id)!!.name, equalTo(ogName))
        scratchManagement.editDatasetName(ds, newName)
        assertThat(scratchManagement.getDatasetById(ds.id)!!.name, equalTo(newName))
    }

    @Test
    fun test_addCategoryToDataset_categoryNotPresent() = runBlocking {
        val fakeDs = FirestoreDataset("path", getRandomString(), getRandomString(), setOf())
        runCatching {
            scratchManagement.addCategoryToDataset(fakeDs, fakeCategory)
        }.fold({
            fail("unexpected successful completion")
        }, {
            assertThat(it, instanceOf(IllegalArgumentException::class.java))
        })
    }
}
