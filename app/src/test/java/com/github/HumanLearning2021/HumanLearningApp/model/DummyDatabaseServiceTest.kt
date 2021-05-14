package com.github.HumanLearning2021.HumanLearningApp.model

import android.net.Uri
import com.github.HumanLearning2021.HumanLearningApp.R
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner


@RunWith(RobolectricTestRunner::class)
class DummyDatabaseServiceTest {
    private val forkUri =
        Uri.parse("android.resource://com.github.HumanLearning2021.HumanLearningApp/" + R.drawable.fork)
    private val knifeUri =
        Uri.parse("android.resource://com.github.HumanLearning2021.HumanLearningApp/" + R.drawable.knife)
    private val spoonUri =
        Uri.parse("android.resource://com.github.HumanLearning2021.HumanLearningApp/" + R.drawable.spoon)

    lateinit var dummyDatabaseService: DummyDatabaseService

    @Before
    fun setUp() {
        dummyDatabaseService = DummyDatabaseService()
    }

    @ExperimentalCoroutinesApi
    @Suppress("DEPRECATION")
    @Test(expected = DatabaseService.NotFoundException::class)
    fun getPictureCategoryNotPresentThrows() = runBlockingTest {
        dummyDatabaseService.getPicture(DummyCategory("Plate", "Plate"))
        Unit
    }

    @ExperimentalCoroutinesApi
    @Suppress("DEPRECATION")
    @Test
    fun getPictureCategoryEmpty() = runBlockingTest {
        assertThat(
            dummyDatabaseService.getPicture(dummyDatabaseService.putCategory("Plate")), equalTo(
                null
            )
        )
    }

    @ExperimentalCoroutinesApi
    @Suppress("DEPRECATION")
    @Test
    fun putAndThenGetWorks() = runBlockingTest {
        val table = dummyDatabaseService.putCategory("Table")
        dummyDatabaseService.putPicture(forkUri, table)

        val res = dummyDatabaseService.getPicture(table)!!
        assertThat(res.category, equalTo(table))
    }

    @ExperimentalCoroutinesApi
    @Test(expected = DatabaseService.NotFoundException::class)
    fun putPictureCategoryNotPresentThrows() = runBlockingTest {
        val table = dummyDatabaseService.putCategory("Table")
        dummyDatabaseService.removeCategory(table)
        dummyDatabaseService.putPicture(forkUri, table)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getCategoryPresent() = runBlockingTest {
        dummyDatabaseService.putCategory("Table")
        assertThat(
            dummyDatabaseService.getCategory("Table"),
            equalTo(DummyCategory("Table", "Table"))
        )
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getCategoryNotPresent() = runBlockingTest {
        assertThat(dummyDatabaseService.getCategory("Table"), equalTo(null))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun putCategoryNotPresent() = runBlockingTest {
        assertThat(
            dummyDatabaseService.putCategory("Table"),
            equalTo(DummyCategory("Table", "Table"))
        )
    }

    @ExperimentalCoroutinesApi
    @Test
    fun putCategoryAlreadyPresentChangesNothing() = runBlockingTest {
        dummyDatabaseService.putCategory("Table")
        assertThat(
            dummyDatabaseService.putCategory("Table"),
            equalTo(DummyCategory("Table", "Table"))
        )
    }

    @ExperimentalCoroutinesApi
    @Test(expected = DatabaseService.NotFoundException::class)
    fun removeCategoryThrowsExpectedException() = runBlockingTest {
        dummyDatabaseService.removeCategory(Category("Table", "Table"))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun removeCategoryWorks() = runBlockingTest {
        val fork = dummyDatabaseService.putCategory("Fork")
        dummyDatabaseService.removeCategory(fork)
        assert(!dummyDatabaseService.getCategories().contains(fork))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun removeCategoryAlsoRemovesFromDatasets() = runBlockingTest {
        val fork = dummyDatabaseService.putCategory("Fork")
        val spoon = dummyDatabaseService.putCategory("Spoon")
        dummyDatabaseService.putDataset("Utensils", setOf(fork, spoon))
        dummyDatabaseService.removeCategory(fork)
        assert(!dummyDatabaseService.getDataset("Utensils")!!.categories.contains(fork))
    }

    @ExperimentalCoroutinesApi
    @Test(expected = DatabaseService.NotFoundException::class)
    fun putRepresentativePictureThrowsExpectedException() = runBlockingTest {
        val table = Category("Table", "Table")
        dummyDatabaseService.putRepresentativePicture(Uri.EMPTY, table)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun putRepresentativePictureWorks() = runBlockingTest {
        val table = dummyDatabaseService.putCategory("Table")
        assert(dummyDatabaseService.getRepresentativePicture("Table") == null)
        dummyDatabaseService.putRepresentativePicture(Uri.EMPTY, table)
        assert(dummyDatabaseService.getRepresentativePicture("Table")!!.category == table)
    }

    @ExperimentalCoroutinesApi
    @Test(expected = DatabaseService.NotFoundException::class)
    fun deleteDatasetThrowsNotFoundException() = runBlockingTest {
        dummyDatabaseService.deleteDataset("Fork")
    }

    @ExperimentalCoroutinesApi
    @Test
    fun deleteDatasetWorks() = runBlockingTest {
        val fork = dummyDatabaseService.putCategory("Fork")
        val spoon = dummyDatabaseService.putCategory("Spoon")
        dummyDatabaseService.putDataset("Fork", setOf(fork, spoon))
        dummyDatabaseService.deleteDataset("Fork")
        assert(dummyDatabaseService.getDataset("Fork") == null)
    }

    @ExperimentalCoroutinesApi
    @Test(expected = DatabaseService.NotFoundException::class)
    fun getAllPicturesThrowsNotFoundException() = runBlockingTest {
        val table = Category("Table", "Table")
        dummyDatabaseService.getAllPictures(table)
        Unit
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getAllPicturesGetsAllPictures() = runBlockingTest {
        val fork = dummyDatabaseService.putCategory("Fork")
        dummyDatabaseService.putPicture(forkUri, fork)
        dummyDatabaseService.putPicture(spoonUri, fork)
        val res = dummyDatabaseService.getAllPictures(fork).map { p ->
            DummyCategorizedPicture(
                "forkpicid",
                p.category,
                (p as DummyCategorizedPicture).picture
            )
        }
        assert(
            res.containsAll(
                setOf(
                    DummyCategorizedPicture("forkpicid", fork, forkUri),
                    DummyCategorizedPicture("forkpicid", fork, spoonUri)
                )
            )
        )
    }

    @ExperimentalCoroutinesApi
    @Test(expected = DatabaseService.NotFoundException::class)
    fun removePictureThrowsNotFoundException() = runBlockingTest {
        dummyDatabaseService.removePicture(
            DummyCategorizedPicture(
                "tablepicid",
                DummyCategory("Table", "Table"),
                Uri.EMPTY
            )
        )
    }

    @ExperimentalCoroutinesApi
    @Test
    fun removePictureRemovesPicture() = runBlockingTest {
        val fork = dummyDatabaseService.putCategory("Fork")
        val forkPic = dummyDatabaseService.putPicture(forkUri, fork)
        dummyDatabaseService.removePicture(forkPic)
        assert(dummyDatabaseService.getAllPictures(fork).isEmpty())
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getDatasetReturnsNullIfThereIsNone() = runBlockingTest {
        assert(dummyDatabaseService.getDataset("Fork") == null)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getDatasetsWorks() = runBlockingTest {
        dummyDatabaseService.putDataset("kitchen utensils", setOf())
        assert(
            dummyDatabaseService.getDatasets().contains(
                DummyDataset(
                    "kitchen utensils",
                    "kitchen utensils",
                    setOf()
                )
            )
        )
    }

    @ExperimentalCoroutinesApi
    @Test(expected = DatabaseService.NotFoundException::class)
    fun removeCategoryFromDatasetThrowsNotFoundExceptionIfCategoryNotInDb() = runBlockingTest {
        val name = "Utensils"
        val fork = DummyCategory("Fork", "Fork")
        val dataset = DummyDataset(name, name, setOf())
        dummyDatabaseService.removeCategoryFromDataset(dataset, fork)
        Unit
    }

    @ExperimentalCoroutinesApi
    @Test(expected = DatabaseService.NotFoundException::class)
    fun removeCategoryFromDatasetThrowsNotFoundExceptionIfDatasetNotInDb() = runBlockingTest {
        val fork = dummyDatabaseService.putCategory("Fork")
        dummyDatabaseService.removeCategoryFromDataset(
            DummyDataset(
                "some_id",
                "some_name",
                setOf()
            ), fork
        )
        Unit
    }

    @ExperimentalCoroutinesApi
    @Test
    fun removeCategoryFromDatasetWorks() = runBlockingTest {
        val fork = DummyCategory("Fork", "Fork")
        val knife = DummyCategory("Knife", "Knife")
        val spoon = DummyCategory("Spoon", "Spoon")
        val name = "Utensils"
        val dataset = dummyDatabaseService.putDataset(name, setOf(fork, knife, spoon))
        val newDataset = dummyDatabaseService.removeCategoryFromDataset(dataset, fork)
        assert(newDataset.categories.containsAll(setOf(knife, spoon)))
        assert(!newDataset.categories.contains(fork))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun editDatasetNameWorks() = runBlockingTest {
        val fork = DummyCategory("Fork", "Fork")
        val knife = DummyCategory("Knife", "Knife")
        val spoon = DummyCategory("Spoon", "Spoon")
        val name = "Utensils"
        val newName = "NoLongerUtensils"
        val dataset = dummyDatabaseService.putDataset(name, setOf(fork, knife, spoon))
        val newDataset = dummyDatabaseService.editDatasetName(dataset, newName)
        assert(newDataset.categories.containsAll(dataset.categories))
        assert(newDataset.name == newName)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun addCategoryToDatasetWorks() = runBlockingTest {
        val fork = dummyDatabaseService.putCategory("Fork")
        val knife = dummyDatabaseService.putCategory("Knife")
        val spoon = dummyDatabaseService.putCategory("Spoon")
        val name = "Utensils"
        val dataset = dummyDatabaseService.putDataset(name, setOf(fork, knife))
        require(dataset.categories.containsAll(setOf(fork, knife)))
        require(dataset.categories.size == 2)
        val newDataset = dummyDatabaseService.addCategoryToDataset(dataset, spoon)
        assertThat(newDataset.categories, equalTo(setOf(fork, knife, spoon)))
    }

    @ExperimentalCoroutinesApi
    @Test(expected = DatabaseService.NotFoundException::class)
    fun addCategoryToDatasetThrowsIfCategoryNotInDatabase() = runBlockingTest {
        val fork = DummyCategory("Fork", "Fork")
        val knife = DummyCategory("Knife", "Knife")
        val name = "Utensils"
        val dataset = dummyDatabaseService.putDataset(name, setOf(fork, knife))
        dummyDatabaseService.addCategoryToDataset(dataset, Category("Table", "Table"))
        Unit
    }

    @ExperimentalCoroutinesApi
    @Test(expected = DatabaseService.NotFoundException::class)
    fun addCategoryToDatasetThrowsIfDatasetNotInDatabase() = runBlockingTest {
        val spoon = dummyDatabaseService.putCategory("Spoon")
        dummyDatabaseService.addCategoryToDataset(
            DummyDataset("some_id", "some_name", setOf()),
            spoon
        )
        Unit
    }
}

