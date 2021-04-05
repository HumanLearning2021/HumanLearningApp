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
import org.junit.Test
import java.io.File
import java.lang.IllegalArgumentException
import java.util.*

class FirestoreDatabaseServiceTest : TestCase() {
    lateinit var demoInterface: FirestoreDatabaseService
    lateinit var scratchInterface: FirestoreDatabaseService
    lateinit var appleCategoryId: String
    lateinit var fakeCategory: FirestoreCategory
    lateinit var fakeDataset: FirestoreDataset

    override fun setUp() {
        demoInterface = FirestoreDatabaseService("demo")
        scratchInterface = FirestoreDatabaseService("scratch")
        appleCategoryId = "LbaIwsl1kizvTod4q1TG"
        fakeCategory =  FirestoreCategory("oopsy", "oopsy", "oopsy", null)
        fakeDataset = FirestoreDataset("oopsy", "oopsy", "oopsy", setOf())
    }

    fun test_getCategories() = runBlocking {
        val cats = demoInterface.getCategories()
        assertThat(cats, hasItem(hasName("Pomme")))
    }

    fun test_getCategory() = runBlocking {
        val cat = demoInterface.getCategory(appleCategoryId)
        assertThat(cat, hasName("Pomme"))
    }

    fun test_getPicture() = runBlocking {
        val appleCategory = demoInterface.getCategories().find { it.name == "Pomme" }
        requireNotNull(appleCategory, { "category of apples no found in demo database" })
        val pic = demoInterface.getPicture(appleCategory)
        assertThat(pic, hasCategory(equalTo(appleCategory)))
    }

    fun test_putCategory() = runBlocking {
        val cat = scratchInterface.putCategory("Poire")
        assertThat(cat, hasName("Poire"))
    }

    fun test_putPicture() = runBlocking {
        val ctx = ApplicationProvider.getApplicationContext<Context>()
        val cat = scratchInterface.putCategory("Poire")

        val tmp = File.createTempFile("meow", ".png")
        val pic = try {
            ctx.resources.openRawResource(R.drawable.fork).use { img ->
                tmp.outputStream().use {
                    img.copyTo(it)
                }
            }
            val uri = Uri.fromFile(tmp)
            scratchInterface.putPicture(uri, cat)
        } finally {
            tmp.delete()
        }
        assertThat(pic, hasCategory(equalTo(cat)))
    }

    fun test_getAllPictures() = runBlocking {
        val fruitCategory = demoInterface.getCategory(appleCategoryId)
        requireNotNull(fruitCategory, {"fruit category not found in demo database"})
        val pics = demoInterface.getAllPictures(fruitCategory)
        assertThat(pics.size, equalTo(2))
        for (p in pics) {
            assertThat(p, hasCategory(equalTo(fruitCategory)))
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun test_getAllPictures_throwsIfCategoryNotPresent(): Unit = runBlocking {
        demoInterface.getAllPictures(fakeCategory)
    }

    @Test(expected = IllegalArgumentException::class)
    fun test_removeCategory_throws(): Unit = runBlocking {
        demoInterface.removeCategory(fakeCategory)
    }

    fun test_putThenRemoveCategory() = runBlocking {
        val randomCategoryName = "${UUID.randomUUID()}"
        val testCategory = scratchInterface.putCategory(randomCategoryName)
        assertThat(scratchInterface.getCategory(testCategory.id), hasName(equalTo(randomCategoryName)))
        scratchInterface.removeCategory(testCategory)
        assertThat(scratchInterface.getCategory(testCategory.id), equalTo(null))
    }

    @Test(expected = IllegalArgumentException::class)
    fun test_removePicture_throwsIfCategoryNotPresent(): Unit = runBlocking {
        demoInterface.removePicture(FirestoreCategorizedPicture("some_Path", fakeCategory, "some url"))
    }

    fun test_removePicture() = runBlocking {
        val randomCategoryName = "${UUID.randomUUID()}"
        val ctx = ApplicationProvider.getApplicationContext<Context>()
        val aThing = scratchInterface.putCategory(randomCategoryName)
        val tmp = File.createTempFile("nobodyexpectsthespanishinquisition", ".png")
        val pic = try {
            ctx.resources.openRawResource(R.drawable.fork).use { img ->
                tmp.outputStream().use {
                    img.copyTo(it)
                }
            }
            val uri = Uri.fromFile(tmp)
            scratchInterface.putPicture(uri, aThing)
        } finally {
            tmp.delete()
        }
        require(scratchInterface.getPicture(aThing) != null) { "picture was not put into database" }

        scratchInterface.removePicture(pic)
        assertThat(scratchInterface.getPicture(aThing), equalTo(null))
    }

    fun test_putDataset_emptyCategories() = runBlocking {
        val randomDatasetName = "${UUID.randomUUID()}"
        val ds = scratchInterface.putDataset(randomDatasetName, setOf())
        assertThat(scratchInterface.getDataset(ds.id)!!.name, equalTo(randomDatasetName))
    }

    fun test_putDataset_wCategories() = runBlocking {
        val randomDatasetName = "${UUID.randomUUID()}"
        val randomCategoryName1 = "${UUID.randomUUID()}"
        val randomCategoryName2 = "${UUID.randomUUID()}"
        val cat1 = scratchInterface.putCategory(randomCategoryName1)
        val cat2 = scratchInterface.putCategory(randomCategoryName2)
        val ds = scratchInterface.putDataset(randomDatasetName, setOf(cat1, cat2))
        assertThat(scratchInterface.getDataset(ds.id)!!.name, equalTo(randomDatasetName))
        assertThat(scratchInterface.getDataset(ds.id)!!.categories, equalTo(setOf(cat1, cat2)))
    }

    fun test_getDataset() = runBlocking {
        assertThat(demoInterface.getDataset("PzuR0B48GpYN5ERxM3DW")!!.name, equalTo("Fruit"))
    }

    fun test_getDataset_na() = runBlocking {
        val randomDatasetName = "${UUID.randomUUID()}"
        assertThat(demoInterface.getDataset(randomDatasetName), equalTo(null))
    }

    @Test(expected = IllegalArgumentException::class)
    fun test_deleteDataset_throwsIfDatasetNotPresent(): Unit = runBlocking {
        val randomDatasetName = "${UUID.randomUUID()}"
        demoInterface.deleteDataset(randomDatasetName)
    }

    fun test_deleteDataset() = runBlocking {
        val randomDatasetName = "${UUID.randomUUID()}"
        val ds = scratchInterface.putDataset(randomDatasetName, setOf())
        require(scratchInterface.getDataset(ds.id) != null) { "dataset was not put into database" }
        scratchInterface.deleteDataset(ds.id)
        assertThat(scratchInterface.getDataset(ds.id), equalTo(null))
    }

    @Test(expected = IllegalArgumentException::class)
    fun test_putRepresentativePicture_throwsIfCategoryNotPresent() = runBlocking {
        demoInterface.putRepresentativePicture(Uri.EMPTY, fakeCategory)
    }

    fun test_putRepresentativePicture() = runBlocking {
        val randomCategoryName = "${UUID.randomUUID()}"
        val ctx = ApplicationProvider.getApplicationContext<Context>()
        val aThing = scratchInterface.putCategory(randomCategoryName)
        val tmp = File.createTempFile("nobodyExpectsTheSpanishInquisition", ".png")
        val pic = try {
            ctx.resources.openRawResource(R.drawable.fork).use { img ->
                tmp.outputStream().use {
                    img.copyTo(it)
                }
            }
            val uri = Uri.fromFile(tmp)

            scratchInterface.putRepresentativePicture(uri, aThing)
        } finally {
            tmp.delete()
        }

        assertThat(scratchInterface.getCategory(aThing.id)!!.representativePicture, equalTo(pic))
    }

    fun test_getDatasets() = runBlocking {
        assertThat(demoInterface.getDatasets().first().name, equalTo("Fruit"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun test_removeCategoryFromDataset_throwsIfDatasetNotContained(): Unit = runBlocking {
        val cat = demoInterface.getCategories().first()
        demoInterface.removeCategoryFromDataset(fakeDataset, cat)
    }

    @Test(expected = IllegalArgumentException::class)
    fun test_removeCategoryFromDataset_throwsIfCategoryNotContained(): Unit = runBlocking {
        val ds = demoInterface.getDatasets().first()
        demoInterface.removeCategoryFromDataset(ds, fakeCategory)
    }

    fun test_removeCategoryFromDataset() = runBlocking {
        val randomCategoryName = "${UUID.randomUUID()}"
        val randomDatasetName = "${UUID.randomUUID()}"
        val cat = scratchInterface.putCategory(randomCategoryName)
        require(scratchInterface.getCategory(cat.id) != null) { "category was not put into database" }
        var ds = scratchInterface.putDataset(randomDatasetName, setOf(cat))
        require(scratchInterface.getDataset(ds.id) != null) { "dataset was not put into database" }
        ds = scratchInterface.removeCategoryFromDataset(ds, cat)
        assertThat(scratchInterface.getDataset(ds.id), not(equalTo(null)))
        assertThat(scratchInterface.getDataset(ds.id)!!.categories, not(hasItem(hasName(cat.name))))
        assertThat(scratchInterface.getCategory(ds.id), not(equalTo(null)))
    }

    @Test(expected = IllegalArgumentException::class)
    fun test_editDatasetName_throwsIfDatasetNotPresent(): Unit = runBlocking {
        demoInterface.editDatasetName(fakeDataset, "Some name")
    }

    fun test_editDatasetName() = runBlocking {
        val newDsName = "An epic new name"
        val randomDatasetName = "${UUID.randomUUID()}"
        val ds = scratchInterface.putDataset(randomDatasetName, setOf())
        require(scratchInterface.getDataset(ds.id) != null) { "dataset was not put into database" }
        require(scratchInterface.getDataset(ds.id)!!.name == randomDatasetName) { "dataset has incorrect name" }
        scratchInterface.editDatasetName(ds, newDsName)
        assertThat(scratchInterface.getDataset(ds.id)!!.name, equalTo(newDsName))
    }
}

