package com.github.HumanLearning2021.HumanLearningApp.firestore

import android.content.Context
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.hilt.ScratchDatabase
import com.github.HumanLearning2021.HumanLearningApp.hilt.TestDatabase
import com.github.HumanLearning2021.HumanLearningApp.model.*
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
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
import java.util.*
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class FirestoreDatabaseManagementTest {
    @Inject
    @TestDatabase
    lateinit var dbMgt: DatabaseManagement

    @Inject
    @ScratchDatabase
    lateinit var scratchManagement: DatabaseManagement

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    private val appleCategoryId = "LbaIwsl1kizvTod4q1TG"
    private val pearCategoryId = "T4UkpkduhRtvjdCDqBFz"
    private val fakeCategory = Category("oopsy", "oopsy")

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    private fun getRandomString() = "${UUID.randomUUID()}"

    @Test
    fun test_getPicture_categoryNotPresent() = runBlocking {
        runCatching {
            @Suppress("DEPRECATION")
            scratchManagement.getPicture(Category(getRandomString(), getRandomString()))
        }.fold({
            fail("unexpected successful completion")
        }, {
            assertThat(it, instanceOf(DatabaseService.NotFoundException::class.java))
        })
    }

    @Test
    fun test_getPicture() = runBlocking {
        val appleCategory = dbMgt.getCategoryById(appleCategoryId)
        requireNotNull(appleCategory, { "category of apples no found in demo database" })
        @Suppress("DEPRECATION") val pic = dbMgt.getPicture(appleCategory)
        assertThat(pic, hasCategory(equalTo(appleCategory)))
    }

    @Test
    fun test_getRepresentativePicture_null() = runBlocking {
        assertThat(dbMgt.getRepresentativePicture(pearCategoryId), equalTo(null))
    }

    @Test
    fun test_getRepresentativePicture() = runBlocking {
        val appleCategory = dbMgt.getCategoryById(appleCategoryId)
        assertThat(
            dbMgt.getRepresentativePicture(appleCategoryId),
            hasCategory(equalTo(appleCategory))
        )
    }

    @Test
    fun test_putPicture_categoryNotPresent() = runBlocking {
        runCatching {
            val tmp = File.createTempFile("meow", ".png")
            val uri = Uri.fromFile(tmp)
            scratchManagement.putPicture(
                uri,
                Category(getRandomString(), getRandomString())
            )
            tmp.delete()
        }.fold({
            fail("unexpected successful completion")
        }, {
            assertThat(it, instanceOf(DatabaseService.NotFoundException::class.java))
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
        assertThat(dbMgt.getCategoryById(getRandomString()), equalTo(null))
    }

    @Test
    fun test_getCategoryById() = runBlocking {
        assertThat(dbMgt.getCategoryById(appleCategoryId), hasName("Pomme"))
    }

    @Test
    fun test_getCategoryByName() = runBlocking {
        assertThat(dbMgt.getCategoryByName("Poire").first(), hasName("Poire"))
    }

    @Test
    fun test_putCategory() = runBlocking {
        val name = getRandomString()
        assertThat(scratchManagement.putCategory(name), hasName(name))
    }

    @Test
    fun test_getCategories() = runBlocking {
        val cats = dbMgt.getCategories()
        assertThat(cats, hasItems(hasName("Pomme")))
    }

    @Test
    fun test_getAllPictures_categoryNotPresent() = runBlocking {
        runCatching {
            scratchManagement.getAllPictures(fakeCategory)
        }.fold({
            fail("unexpected successful completion")
        }, {
            assertThat(it, instanceOf(DatabaseService.NotFoundException::class.java))
        })
    }

    @Test
    fun test_getAllPictures() = runBlocking {
        val cat = dbMgt.getCategoryById(appleCategoryId)
        val pics = dbMgt.getAllPictures(cat!!)
        assertThat(pics, hasSize(5))
        assertThat(pics, hasItems(hasCategory(equalTo(cat))))
    }

    @Test
    fun test_removeCategory() = runBlocking {
        val cat = scratchManagement.putCategory(getRandomString())
        requireNotNull(
            scratchManagement.getCategoryById(cat.id),
            { "category was not put into database" })
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
        @Suppress("DEPRECATION")
        assertThat(scratchManagement.getPicture(cat), equalTo(null))
    }

    @Test
    fun test_getPictureIds_throwsIfCategoryNotPresent() = runBlocking {
        runCatching {
            scratchManagement.getAllPictures(fakeCategory)
        }.fold({
            fail("unexpected successful completion")
        }, {
            assertThat(it, instanceOf(DatabaseService.NotFoundException::class.java))
        })
    }

    @Test
    fun test_getPictureIds() = runBlocking {
        val ids = dbMgt.getPictureIds(dbMgt.getCategoryById(appleCategoryId)!!)
        assertThat(ids, hasSize(5))
        assertThat(
            ids,
            containsInAnyOrder("apple01", "apple02", "apple03", "weird_apple", "minecraft_apple")
        )
    }

    @Test
    fun test_getPictureById() = runBlocking {
        val pic = dbMgt.getPicture("apple01")!!
        assertThat(pic.category, equalTo(dbMgt.getCategoryById(appleCategoryId)))
    }

    @Test
    fun test_putDataset() = runBlocking {
        val name = getRandomString()
        val ds = scratchManagement.putDataset(name, setOf())
        assertThat(scratchManagement.getDatasetById(ds.id)!!.name, equalTo(name))
    }

    @Test
    fun test_getDatasetById() = runBlocking {
        assertThat(dbMgt.getDatasetById("PzuR0B48GpYN5ERxM3DW"), not(equalTo(null)))
    }

    @Test
    fun test_getDatasetByName() = runBlocking {
        assertThat(dbMgt.getDatasetByName("Fruit").size, equalTo(1))
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
            assertThat(it, instanceOf(DatabaseService.NotFoundException::class.java))
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
            scratchManagement.putRepresentativePicture(
                CategorizedPicture(
                    "${UUID.randomUUID()}",
                    fakeCategory,
                    Uri.EMPTY
                )
            )
        }.fold({
            fail("unexpected successful completion")
        }, {
            assertThat(it, instanceOf(DatabaseService.NotFoundException::class.java))
        })
    }

    @Test
    fun test_putRepresentativePicture_fromCategorizedPicture() = runBlocking {
        val randomCategoryName = getRandomString()
        val ctx = ApplicationProvider.getApplicationContext<Context>()
        val cat = scratchManagement.putCategory(randomCategoryName)
        val tmp = File.createTempFile("droid", ".png")
        val pic: CategorizedPicture
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
        assertThat(dbMgt.getDatasets().size, equalTo(1))
        assertThat(dbMgt.getDatasets().first().name, equalTo("Fruit"))
    }

    @Test
    fun test_getDatasetNames() = runBlocking {
        assertThat(dbMgt.getDatasetNames().size, equalTo(1))
        assertThat(dbMgt.getDatasetNames().first(), equalTo("Fruit"))
    }

    @Test
    fun test_getDatasetIds() = runBlocking {
        assertThat(dbMgt.getDatasetIds().size, equalTo(1))
        assertThat(dbMgt.getDatasetIds().first(), equalTo("PzuR0B48GpYN5ERxM3DW"))
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
        val fakeDs = Dataset(getRandomString(), getRandomString(), setOf())
        runCatching {
            scratchManagement.editDatasetName(fakeDs, getRandomString())
        }.fold({
            fail("unexpected successful completion")
        }, {
            assertThat(it, instanceOf(DatabaseService.NotFoundException::class.java))
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
        val fakeDs = Dataset(getRandomString(), getRandomString(), setOf())
        runCatching {
            scratchManagement.addCategoryToDataset(fakeDs, fakeCategory)
        }.fold({
            fail("unexpected successful completion")
        }, {
            assertThat(it, instanceOf(DatabaseService.NotFoundException::class.java))
        })
    }
}
