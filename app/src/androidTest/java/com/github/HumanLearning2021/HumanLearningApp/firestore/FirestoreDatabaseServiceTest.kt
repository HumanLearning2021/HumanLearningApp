package com.github.HumanLearning2021.HumanLearningApp.firestore

import android.content.Context
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.hilt.DemoDatabase
import com.github.HumanLearning2021.HumanLearningApp.hilt.ScratchDatabase
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseService
import com.github.HumanLearning2021.HumanLearningApp.model.hasCategory
import com.github.HumanLearning2021.HumanLearningApp.model.hasName
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.util.*
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class FirestoreDatabaseServiceTest {

    @Inject
    @DemoDatabase
    lateinit var demoInterface: DatabaseService

    @Inject
    @ScratchDatabase
    lateinit var scratchInterface: DatabaseService

    @get:Rule
    val hiltRule = HiltAndroidRule(this)
    lateinit var appleCategoryId: String
    lateinit var pearCategoryId: String
    lateinit var fakeCategory: FirestoreCategory
    lateinit var fakeDataset: FirestoreDataset

    @Before
    fun setUp() {
        hiltRule.inject()
        appleCategoryId = "LbaIwsl1kizvTod4q1TG"
        pearCategoryId = "T4UkpkduhRtvjdCDqBFz"
        fakeCategory = FirestoreCategory("oopsy", "oopsy")
        fakeDataset = FirestoreDataset("oopsy", "oopsy", setOf())
    }

    @Test
    fun test_getAllPictures() = runBlocking {
        val appleCategory = demoInterface.getCategory(appleCategoryId)
        requireNotNull(appleCategory, { "apple category not found in demo database" })
        val pics = demoInterface.getAllPictures(appleCategory)
        assertThat(pics, hasSize(5))
        for (p in pics) {
            assertThat(p, hasCategory(equalTo(appleCategory)))
        }
    }

    @Test
    fun test_getAllPictures_throwsIfCategoryNotPresent(): Unit = runBlocking {
        runCatching {
            demoInterface.getAllPictures(fakeCategory)
        }.fold({
            fail("unexpected successful completion")
        }, {
            assertThat(it, instanceOf(DatabaseService.NotFoundException::class.java))
        })
    }

    @Test
    fun test_removeCategory_throws(): Unit = runBlocking {
        runCatching {
            demoInterface.removeCategory(fakeCategory)
        }.fold({
            fail("unexpected successful completion")
        }, {
            assertThat(it, instanceOf(DatabaseService.NotFoundException::class.java))
        })
    }

    @Test
    fun test_putThenRemoveCategory() = runBlocking {
        val randomCategoryName = "${UUID.randomUUID()}"
        val testCategory = scratchInterface.putCategory(randomCategoryName)
        assertThat(
            scratchInterface.getCategory(testCategory.id),
            hasName(equalTo(randomCategoryName))
        )
        scratchInterface.removeCategory(testCategory)
        assertThat(scratchInterface.getCategory(testCategory.id), equalTo(null))
    }

    @Test
    fun test_removePicture_throwsIfPictureNotPresent(): Unit = runBlocking {
        runCatching {
            scratchInterface.removePicture(
                FirestoreCategorizedPicture(
                    "some id",
                    fakeCategory,
                    "some url"
                )
            )
        }.fold({
            fail("unexpected successful completion")
        }, {
            assertThat(it, instanceOf(DatabaseService.NotFoundException::class.java))
        })
    }

    @Suppress("DEPRECATION")
    @Test
    fun test_removePicture() = runBlocking {
        val randomCategoryName = "${UUID.randomUUID()}"
        val ctx = ApplicationProvider.getApplicationContext<Context>()
        val aThing = scratchInterface.putCategory(randomCategoryName)
        val tmp = File.createTempFile("droid", ".png")
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

    @Test
    fun test_putDataset_emptyCategories() = runBlocking {
        val randomDatasetName = "${UUID.randomUUID()}"
        val ds = scratchInterface.putDataset(randomDatasetName, setOf())
        assertThat(scratchInterface.getDataset(ds.id)!!.name, equalTo(randomDatasetName))
    }

    @Test
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

    @Test
    fun test_getDataset() = runBlocking {
        assertThat(demoInterface.getDataset("PzuR0B48GpYN5ERxM3DW")!!.name, equalTo("Fruit"))
    }

    @Test
    fun test_getDataset_na() = runBlocking {
        val randomDatasetName = "${UUID.randomUUID()}"
        assertThat(demoInterface.getDataset(randomDatasetName), equalTo(null))
    }

    @Test
    fun test_deleteDataset_throwsIfDatasetNotPresent(): Unit = runBlocking {
        val randomDatasetName = "${UUID.randomUUID()}"
        runCatching {
            demoInterface.deleteDataset(randomDatasetName)
        }.fold({
            fail("unexpected successful completion")
        }, {
            assertThat(it, instanceOf(DatabaseService.NotFoundException::class.java))
        })
    }

    @Test
    fun test_deleteDataset() = runBlocking {
        val randomDatasetName = "${UUID.randomUUID()}"
        val ds = scratchInterface.putDataset(randomDatasetName, setOf())
        require(scratchInterface.getDataset(ds.id) != null) { "dataset was not put into database" }
        scratchInterface.deleteDataset(ds.id)
        assertThat(scratchInterface.getDataset(ds.id), equalTo(null))
    }

    @Test
    fun test_putRepresentativePicture_throwsIfCategoryNotPresent() = runBlocking {
        runCatching {
            demoInterface.putRepresentativePicture(Uri.EMPTY, fakeCategory)
        }.fold({
            fail("unexpected successful completion")
        }, {
            assertThat(it, instanceOf(DatabaseService.NotFoundException::class.java))
        })
    }

    @Test
    fun test_putRepresentativePicture() = runBlocking {
        val randomCategoryName = "${UUID.randomUUID()}"
        val ctx = ApplicationProvider.getApplicationContext<Context>()
        val cat = scratchInterface.putCategory(randomCategoryName)
        val tmp = File.createTempFile("droid", ".png")
        try {
            ctx.resources.openRawResource(R.drawable.fork).use { img ->
                tmp.outputStream().use {
                    img.copyTo(it)
                }
            }
            val uri = Uri.fromFile(tmp)
            scratchInterface.putRepresentativePicture(uri, cat)
        } finally {
            tmp.delete()
        }

        assertThat(scratchInterface.getRepresentativePicture(cat.id), not(equalTo(null)))
    }

    @Test
    fun test_getDatasets() = runBlocking {
        assertThat(demoInterface.getDatasets().first().name, equalTo("Fruit"))
    }

    @Test
    fun test_removeCategoryFromDataset_throwsIfDatasetNotContained(): Unit = runBlocking {
        val cat = demoInterface.getCategories().first()
        runCatching {
            demoInterface.removeCategoryFromDataset(fakeDataset, cat)
        }.fold({
            fail("unexpected successful completion")
        }, {
            assertThat(it, instanceOf(DatabaseService.NotFoundException::class.java))
        })
    }

    @Test
    fun test_removeCategoryFromDataset_throwsIfCategoryNotContained(): Unit = runBlocking {
        val ds = demoInterface.getDatasets().first()
        runCatching {
            demoInterface.removeCategoryFromDataset(ds, fakeCategory)
        }.fold({
            fail("unexpected successful completion")
        }, {
            assertThat(it, instanceOf(DatabaseService.NotFoundException::class.java))
        })
    }

    @Test
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
        assertThat(scratchInterface.getCategory(cat.id), not(equalTo(null)))
    }

    @Test
    fun test_editDatasetName_throwsIfDatasetNotPresent(): Unit = runBlocking {
        runCatching {
            demoInterface.editDatasetName(fakeDataset, "Some name")
        }.fold({
            fail("unexpected successful completion")
        }, {
            assertThat(it, instanceOf(DatabaseService.NotFoundException::class.java))
        })
    }

    @Test
    fun test_editDatasetName() = runBlocking {
        val newDsName = "An epic new name"
        val randomDatasetName = "${UUID.randomUUID()}"
        val ds = scratchInterface.putDataset(randomDatasetName, setOf())
        require(scratchInterface.getDataset(ds.id) != null) { "dataset was not put into database" }
        require(scratchInterface.getDataset(ds.id)!!.name == randomDatasetName) { "dataset has incorrect name" }
        scratchInterface.editDatasetName(ds, newDsName)
        assertThat(scratchInterface.getDataset(ds.id)!!.name, equalTo(newDsName))
    }

    @Test
    fun test_getRepresentativePicture_null() = runBlocking {
        assertThat(demoInterface.getRepresentativePicture(pearCategoryId), equalTo(null))
    }

    @Test
    fun test_getRepresentativePicture() = runBlocking {
        assertThat(demoInterface.getRepresentativePicture(appleCategoryId), not(equalTo(null)))
    }
}
