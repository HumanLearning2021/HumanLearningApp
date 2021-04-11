package com.github.HumanLearning2021.HumanLearningApp.firestore

import android.content.Context
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.model.hasCategory
import com.github.HumanLearning2021.HumanLearningApp.model.hasName
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers.*
import org.junit.Assert.assertThat
import java.io.File
import java.lang.IllegalArgumentException
import java.util.*

class FirestoreDatabaseManagementTest : TestCase() {

    lateinit var demoManagement: FirestoreDatabaseManagement
    lateinit var scratchManagement: FirestoreDatabaseManagement
    lateinit var appleCategoryId: String
    lateinit var pearCategoryId: String
    lateinit var fakeCategory: FirestoreCategory
    lateinit var fakeDataset: FirestoreDataset

    override fun setUp() {
        demoManagement = FirestoreDatabaseManagement("demo")
        scratchManagement = FirestoreDatabaseManagement("scratch")
        appleCategoryId = "LbaIwsl1kizvTod4q1TG"
        pearCategoryId = "T4UkpkduhRtvjdCDqBFz"
        fakeCategory =  FirestoreCategory("oopsy/oopsy", "oopsy", "oopsy")
        fakeDataset = FirestoreDataset("oopsy/oopsy", "oopsy", "oopsy", setOf())
    }

    private fun getRandomString() = "${UUID.randomUUID()}"

    fun test_getPicture_categoryNotPresent() = runBlocking {
        runCatching {
            scratchManagement.getPicture(FirestoreCategory("path", getRandomString(), name))
        }.fold({
            fail("unexpected successful completion")
        }, {
            assertThat(it, instanceOf(IllegalArgumentException::class.java))
        })
    }

    fun test_getPicture() = runBlocking {
        val appleCategory = demoManagement.getCategoryById(appleCategoryId)
        requireNotNull(appleCategory, { "category of apples no found in demo database" })
        val pic = demoManagement.getPicture(appleCategory)
        assertThat(pic, hasCategory(equalTo(appleCategory)))
    }

    fun test_getRepresentativePicture_null() = runBlocking {
        assertThat(demoManagement.getRepresentativePicture(pearCategoryId), equalTo(null))
    }

    fun test_getRepresentativePicture() = runBlocking {
        val appleCategory = demoManagement.getCategoryById(appleCategoryId)
        assertThat(demoManagement.getRepresentativePicture(appleCategoryId), hasCategory(equalTo(appleCategory)))
    }

    fun test_putPicture_categoryNotPresent() = runBlocking {
        runCatching {
            val tmp = File.createTempFile("meow", ".png")
            val uri = Uri.fromFile(tmp)
            scratchManagement.putPicture(uri, FirestoreCategory("path", getRandomString(), name))
            tmp.delete()
        }.fold({
            fail("unexpected successful completion")
        }, {
            assertThat(it, instanceOf(IllegalArgumentException::class.java))
        })
    }

    fun test_putPicture() = runBlocking {
        name = getRandomString()
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

    fun test_getCategoryById_null() = runBlocking {
        assertThat(demoManagement.getCategoryById(getRandomString()), equalTo(null))
    }

    fun test_getCategoryById() = runBlocking {
        assertThat(demoManagement.getCategoryById(appleCategoryId), hasName("Pomme"))
    }

    fun test_getCategoryByName() = runBlocking {
        assertThat(demoManagement.getCategoryByName("Poire").first(), hasName("Poire"))
    }

    fun test_putCategory() = runBlocking {
        name = getRandomString()
        assertThat(scratchManagement.putCategory(name), hasName(name))
    }

    fun test_getCategories() = runBlocking {
        val cats = demoManagement.getCategories()
        assertThat(cats, hasItems(hasName("Pomme")))
    }

    fun test_getAllPictures_categoryNotPresent() = runBlocking {
        runCatching {
            scratchManagement.getAllPictures(fakeCategory)
        }.fold({
            fail("unexpected successful completion")
        }, {
            assertThat(it, instanceOf(IllegalArgumentException::class.java))
        })
    }

    fun test_getAllPictures() = runBlocking {
        val cat = demoManagement.getCategoryById(appleCategoryId)
        val pics = demoManagement.getAllPictures(cat!!)
        assertThat(pics.size, equalTo(2))
        assertThat(pics, hasItems(hasCategory(equalTo(cat))))
    }

    fun test_removeCategory() = runBlocking {
        val cat = scratchManagement.putCategory(getRandomString())
        requireNotNull(scratchManagement.getCategoryById(cat.id), {"category was not put into database"})
        scratchManagement.removeCategory(cat)
        assertThat(scratchManagement.getCategoryById(cat.id), equalTo(null))
    }

    fun test_removePicture() = runBlocking {
        name = getRandomString()
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

    fun test_putDataset() = runBlocking {
        val name = getRandomString()
        val ds = scratchManagement.putDataset(name, setOf())
        assertThat(scratchManagement.getDatasetById(ds.id)!!.name, equalTo(name))
    }

    fun test_getDatasetById() = runBlocking {
        assertThat(demoManagement.getDatasetById("PzuR0B48GpYN5ERxM3DW"), not(equalTo(null)))
    }

    fun test_getDatasetByName() = runBlocking {
        assertThat(demoManagement.getDatasetByName("Fruit").size, equalTo(1))
    }

    fun test_deleteDataset() = runBlocking {
        val ds = scratchManagement.putDataset(getRandomString(), setOf())
        requireNotNull(scratchManagement.getDatasetById(ds.id))
        scratchManagement.deleteDataset(ds.id)
        assertThat(scratchManagement.getDatasetById(ds.id), equalTo(null))
    }

    fun test_putRepresentativePicture_categoryNotPresent() = runBlocking {
        runCatching {
            scratchManagement.putRepresentativePicture(Uri.EMPTY, fakeCategory)
        }.fold({
            fail("unexpected successful completion")
        }, {
            assertThat(it, instanceOf(IllegalArgumentException::class.java))
        })
    }

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

    fun test_getDatasets() = runBlocking {
        assertThat(demoManagement.getDatasets().size, equalTo(1))
        assertThat(demoManagement.getDatasets().first().name, equalTo("Fruit"))
    }

    fun test_getDatasetNames() = runBlocking {
        assertThat(demoManagement.getDatasetNames().size, equalTo(1))
        assertThat(demoManagement.getDatasetNames().first(), equalTo("Fruit"))
    }

    fun test_getDatasetIds() = runBlocking {
        assertThat(demoManagement.getDatasetIds().size, equalTo(1))
        assertThat(demoManagement.getDatasetIds().first(), equalTo("PzuR0B48GpYN5ERxM3DW"))
    }

    fun test_removeCategoryFromDataset_datasetNotPresent() = runBlocking {
        val cat1 = scratchManagement.putCategory(getRandomString())
        val cat2 = scratchManagement.putCategory(getRandomString())
        val fakeDs = FirestoreDataset("path", getRandomString(), getRandomString(), setOf(cat1, cat2))
        val res = scratchManagement.removeCategoryFromDataset(fakeDs, cat2)
        assertEquals(setOf(cat1), res.categories)
    }

    fun test_removeCategoryFromDataset_categoryNotPresent() = runBlocking {
        val cat1 = scratchManagement.putCategory(getRandomString())
        val cat2 = fakeCategory
        val fakeDs = FirestoreDataset("path", getRandomString(), getRandomString(), setOf(cat1, cat2))
        val res = scratchManagement.removeCategoryFromDataset(fakeDs, cat2)
        assertEquals(setOf(cat1), res.categories)
    }

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

    fun test_editDatasetName() = runBlocking {
        val ogName = getRandomString()
        val ds = scratchManagement.putDataset(ogName, setOf())
        val newName = getRandomString()
        assertThat(scratchManagement.getDatasetById(ds.id)!!.name, equalTo(ogName))
        scratchManagement.editDatasetName(ds, newName)
        assertThat(scratchManagement.getDatasetById(ds.id)!!.name, equalTo(newName))
    }
}