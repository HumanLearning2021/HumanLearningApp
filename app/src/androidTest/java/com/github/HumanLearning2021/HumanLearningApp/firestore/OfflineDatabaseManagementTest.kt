package com.github.HumanLearning2021.HumanLearningApp.firestore

import android.content.Context
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.hilt.OfflineTestDatabase
import com.github.HumanLearning2021.HumanLearningApp.hilt.RoomDatabase
import com.github.HumanLearning2021.HumanLearningApp.model.*
import com.github.HumanLearning2021.HumanLearningApp.offline.PictureCache
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
import java.util.*
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class OfflineDatabaseManagementTest {

    @Inject
    @RoomDatabase
    lateinit var room: RoomOfflineDatabase

    @Inject
    @ApplicationContext
    lateinit var context: Context

    @Inject
    @OfflineTestDatabase
    lateinit var dbMgt: DatabaseManagement

    @get:Rule
    val hiltRule = HiltAndroidRule(this)
    lateinit var appleCategoryId: String
    lateinit var pearCategoryId: String
    lateinit var fakeCategory: Category
    lateinit var fakeDataset: Dataset

    @Before
    fun setUp() = runBlocking {
        hiltRule.inject()
        appleCategoryId = "LbaIwsl1kizvTod4q1TG"
        pearCategoryId = "T4UkpkduhRtvjdCDqBFz"
        fakeCategory = Category("oopsy", "oopsy")
        fakeDataset = Dataset("oopsy", "oopsy", setOf())
    }

    @After
    fun tearDown() = runBlocking {
        PictureCache("demo", context).clear()
        room.clearAllTables()
    }

    private fun getRandomString() = "${UUID.randomUUID()}"

    @Test
    fun test_getPicture_categoryNotPresent() = runBlocking {
        runCatching {
            dbMgt.getPicture(Category(getRandomString(), getRandomString()))
        }.fold({
            Assert.fail("unexpected successful completion")
        }, {
            MatcherAssert.assertThat(
                it,
                Matchers.instanceOf(DatabaseService.NotFoundException::class.java)
            )
        })
    }

    @Test
    fun test_getPicture() = runBlocking {
        val appleCategory = dbMgt.getCategoryById(appleCategoryId)
        requireNotNull(appleCategory, { "category of apples no found in demo database" })
        val pic = dbMgt.getPicture(appleCategory)
        MatcherAssert.assertThat(pic!!.category.id, Matchers.equalTo(appleCategory.id))
    }

    @Test
    fun test_getPictureIds() = runBlocking {
        val ids = dbMgt.getPictureIds(dbMgt.getCategoryById(appleCategoryId)!!)
        MatcherAssert.assertThat(ids, Matchers.hasSize(5))
        MatcherAssert.assertThat(
            ids,
            Matchers.containsInAnyOrder(
                "apple01",
                "apple02",
                "apple03",
                "weird_apple",
                "minecraft_apple"
            )
        )
    }

    @Test
    fun test_getPictureById() = runBlocking {
        val appleCategory = dbMgt.getCategoryById(appleCategoryId) as Category
        requireNotNull(appleCategory, { "category of apples no found in demo database" })
        val picId = dbMgt.getPictureIds(appleCategory).random()
        val pic = dbMgt.getPicture(picId)
        MatcherAssert.assertThat(pic!!.category.id, Matchers.equalTo(appleCategory.id))
    }

    @Test
    fun test_getRepresentativePicture_null() = runBlocking {
        MatcherAssert.assertThat(
            dbMgt.getRepresentativePicture(pearCategoryId),
            Matchers.equalTo(null)
        )
    }

    @Test
    fun test_getRepresentativePicture() = runBlocking {
        val appleCategory = dbMgt.getCategoryById(appleCategoryId)
        MatcherAssert.assertThat(
            dbMgt.getRepresentativePicture(appleCategoryId)!!.category.id,
            equalTo(appleCategory!!.id)
        )
    }

    @Test
    fun test_putPicture_categoryNotPresent() = runBlocking {
        runCatching {
            val tmp = File.createTempFile(
                "meow",
                ".png",
                ApplicationProvider.getApplicationContext<Context>().filesDir
            )
            val uri = Uri.fromFile(tmp)
            dbMgt.putPicture(uri, Category(getRandomString(), getRandomString()))
            tmp.delete()
        }.fold({
            Assert.fail("unexpected successful completion")
        }, {
            MatcherAssert.assertThat(
                it,
                Matchers.instanceOf(DatabaseService.NotFoundException::class.java)
            )
        })
    }

    @Test
    fun test_putPicture() = runBlocking {
        val name = getRandomString()
        val ctx = ApplicationProvider.getApplicationContext<Context>()
        val cat = dbMgt.putCategory(name)

        val tmp = File.createTempFile("meow", ".png", ctx.filesDir)
        val pic = try {
            ctx.resources.openRawResource(R.drawable.fork).use { img ->
                tmp.outputStream().use {
                    img.copyTo(it)
                }
            }
            val uri = Uri.fromFile(tmp)
            dbMgt.putPicture(uri, cat)
        } finally {
            tmp.delete()
        }
        MatcherAssert.assertThat(pic.category.id, equalTo(cat.id))
    }

    @Test
    fun test_getCategoryById_null() = runBlocking {
        MatcherAssert.assertThat(
            dbMgt.getCategoryById(getRandomString()),
            Matchers.equalTo(null)
        )
    }

    @Test
    fun test_getCategoryById() = runBlocking {
        MatcherAssert.assertThat(dbMgt.getCategoryById(appleCategoryId), hasName("Pomme"))
    }

    @Test
    fun test_getCategoryByName() = runBlocking {
        MatcherAssert.assertThat(
            dbMgt.getCategoryByName("Poire").first(),
            hasName("Poire")
        )
    }

    @Test
    fun test_putCategory() = runBlocking {
        val name = getRandomString()
        MatcherAssert.assertThat(dbMgt.putCategory(name), hasName(name))
    }

    @Test
    fun test_getCategories() = runBlocking {
        val cats = dbMgt.getCategories()
        MatcherAssert.assertThat(cats, Matchers.hasItems(hasName("Pomme")))
    }

    @Test
    fun test_getAllPictures_categoryNotPresent() = runBlocking {
        runCatching {
            dbMgt.getAllPictures(fakeCategory)
        }.fold({
            Assert.fail("unexpected successful completion")
        }, {
            MatcherAssert.assertThat(
                it,
                Matchers.instanceOf(DatabaseService.NotFoundException::class.java)
            )
        })
    }

    @Test
    fun test_getAllPictures() = runBlocking {
        val cat = dbMgt.getCategoryById(appleCategoryId)
        val pics = dbMgt.getAllPictures(cat!!)
        MatcherAssert.assertThat(pics, Matchers.hasSize(5))
        MatcherAssert.assertThat(
            pics.map { p -> p.category.id },
            Matchers.hasItems(Matchers.equalTo(cat.id))
        )
    }

    @Test
    fun test_removeCategory() = runBlocking {
        val cat = dbMgt.putCategory(getRandomString())
        requireNotNull(
            dbMgt.getCategoryById(cat.id),
            { "category was not put into database" })
        dbMgt.removeCategory(cat)
        MatcherAssert.assertThat(dbMgt.getCategoryById(cat.id), Matchers.equalTo(null))
    }

    @Test
    fun test_removePicture() = runBlocking {
        val name = getRandomString()
        val ctx = ApplicationProvider.getApplicationContext<Context>()
        val cat = dbMgt.putCategory(name)

        val tmp = File.createTempFile("meow", ".png", ctx.filesDir)
        val pic = try {
            ctx.resources.openRawResource(R.drawable.fork).use { img ->
                tmp.outputStream().use {
                    img.copyTo(it)
                }
            }
            val uri = Uri.fromFile(tmp)
            dbMgt.putPicture(uri, cat)
        } finally {
            tmp.delete()
        }
        dbMgt.removePicture(pic)
        MatcherAssert.assertThat(dbMgt.getPicture(pic.id), Matchers.equalTo(null))
    }

    @Test
    fun test_getPictureIds_throwsIfCategoryNotPresent() = runBlocking {
        runCatching {
            dbMgt.getAllPictures(fakeCategory)
        }.fold({
            Assert.fail("unexpected successful completion")
        }, {
            MatcherAssert.assertThat(
                it,
                Matchers.instanceOf(DatabaseService.NotFoundException::class.java)
            )
        })
    }

    @Test
    fun test_putDataset() = runBlocking {
        val name = getRandomString()
        val ds = dbMgt.putDataset(name, setOf())
        MatcherAssert.assertThat(
            dbMgt.getDatasetById(ds.id)!!.name,
            Matchers.equalTo(name)
        )
    }

    @Test
    fun test_getDatasetById() = runBlocking {
        MatcherAssert.assertThat(
            dbMgt.getDatasetById("PzuR0B48GpYN5ERxM3DW"), Matchers.not(
                Matchers.equalTo(null)
            )
        )
    }

    @Test
    fun test_getDatasetByName() = runBlocking {
        MatcherAssert.assertThat(dbMgt.getDatasetByName("Fruit").size, Matchers.equalTo(1))
    }

    @Test
    fun test_deleteDataset() = runBlocking {
        val ds = dbMgt.putDataset(getRandomString(), setOf())
        requireNotNull(dbMgt.getDatasetById(ds.id))
        dbMgt.deleteDataset(ds.id)
        MatcherAssert.assertThat(dbMgt.getDatasetById(ds.id), Matchers.equalTo(null))
    }

    @Test
    fun test_putRepresentativePicture_categoryNotPresent() = runBlocking {
        runCatching {
            dbMgt.putRepresentativePicture(Uri.EMPTY, fakeCategory)
        }.fold({
            Assert.fail("unexpected successful completion")
        }, {
            MatcherAssert.assertThat(
                it,
                Matchers.instanceOf(DatabaseService.NotFoundException::class.java)
            )
        })
    }

    @Test
    fun test_putRepresentativePicture() = runBlocking {
        val randomCategoryName = getRandomString()
        val ctx = ApplicationProvider.getApplicationContext<Context>()
        val cat = dbMgt.putCategory(randomCategoryName)
        val tmp = File.createTempFile("droid", ".png", ctx.filesDir)
        try {
            ctx.resources.openRawResource(R.drawable.fork).use { img ->
                tmp.outputStream().use {
                    img.copyTo(it)
                }
            }
            val uri = Uri.fromFile(tmp)
            dbMgt.putRepresentativePicture(uri, cat)
        } finally {
            tmp.delete()
        }

        MatcherAssert.assertThat(
            dbMgt.getRepresentativePicture(cat.id), Matchers.not(
                Matchers.equalTo(null)
            )
        )
    }

    @Test
    fun test_putRepresentativePicture_fromCategorizedPicture() = runBlocking {
        val randomCategoryName = getRandomString()
        val ctx = ApplicationProvider.getApplicationContext<Context>()
        val cat = dbMgt.putCategory(randomCategoryName)
        val tmp = File.createTempFile("droid", ".png", ctx.filesDir)
        var pic: CategorizedPicture
        try {
            ctx.resources.openRawResource(R.drawable.fork).use { img ->
                tmp.outputStream().use {
                    img.copyTo(it)
                }
            }
            val uri = Uri.fromFile(tmp)
            pic = dbMgt.putPicture(uri, cat)
        } finally {
            tmp.delete()
        }
        Assume.assumeThat(dbMgt.getPictureIds(pic.category), Matchers.hasItem(pic.id))
        dbMgt.putRepresentativePicture(pic)
        MatcherAssert.assertThat(
            dbMgt.getRepresentativePicture(cat.id),
            Matchers.not(Matchers.equalTo(null))
        )
        MatcherAssert.assertThat(
            dbMgt.getPictureIds(pic.category),
            Matchers.not(Matchers.hasItem(pic.id))
        )
    }

    @Test
    fun test_getDatasets() = runBlocking {
        MatcherAssert.assertThat(dbMgt.getDatasets().size, Matchers.equalTo(1))
        MatcherAssert.assertThat(
            dbMgt.getDatasets().first().name,
            Matchers.equalTo("Fruit")
        )
    }

    @Test
    fun test_getDatasetNames() = runBlocking {
        MatcherAssert.assertThat(dbMgt.getDatasetNames().size, Matchers.equalTo(1))
        MatcherAssert.assertThat(
            dbMgt.getDatasetNames().first(),
            Matchers.equalTo("Fruit")
        )
    }

    @Test
    fun test_getDatasetIds() = runBlocking {
        MatcherAssert.assertThat(dbMgt.getDatasetIds().size, Matchers.equalTo(1))
        MatcherAssert.assertThat(
            dbMgt.getDatasetIds().first(),
            Matchers.equalTo("PzuR0B48GpYN5ERxM3DW")
        )
    }

    @Test
    fun test_removeCategoryFromDataset() = runBlocking {
        val cat1 = dbMgt.putCategory(getRandomString())
        val cat2 = dbMgt.putCategory(getRandomString())
        val ds = dbMgt.putDataset(getRandomString(), setOf(cat1, cat2))
        dbMgt.removeCategoryFromDataset(ds, cat2)
        val cats = dbMgt.getDatasetById(ds.id)!!.categories
        MatcherAssert.assertThat(cats.size, Matchers.equalTo(1))
        val catIds = cats.map { c -> c.id }
        assert(catIds.contains(cat1.id))
        assert(!catIds.contains(cat2.id))
    }

    @Test
    fun test_editDatasetName_datasetNotPresent() = runBlocking {
        val fakeDs = Dataset(getRandomString(), getRandomString(), setOf())
        runCatching {
            dbMgt.editDatasetName(fakeDs, getRandomString())
        }.fold({
            Assert.fail("unexpected successful completion")
        }, {
            MatcherAssert.assertThat(
                it,
                Matchers.instanceOf(DatabaseService.NotFoundException::class.java)
            )
        })
    }

    @Test
    fun test_editDatasetName() = runBlocking {
        val ogName = getRandomString()
        val ds = dbMgt.putDataset(ogName, setOf())
        val newName = getRandomString()
        MatcherAssert.assertThat(
            dbMgt.getDatasetById(ds.id)!!.name,
            Matchers.equalTo(ogName)
        )
        dbMgt.editDatasetName(ds, newName)
        MatcherAssert.assertThat(
            dbMgt.getDatasetById(ds.id)!!.name,
            Matchers.equalTo(newName)
        )
    }

    @Test
    fun test_addCategoryToDataset_categoryNotPresent() = runBlocking {
        val fakeDs = Dataset(getRandomString(), getRandomString(), setOf())
        runCatching {
            dbMgt.addCategoryToDataset(fakeDs, fakeCategory)
        }.fold({
            Assert.fail("unexpected successful completion")
        }, {
            MatcherAssert.assertThat(
                it,
                Matchers.instanceOf(DatabaseService.NotFoundException::class.java)
            )
        })
    }
}
