package com.github.HumanLearning2021.HumanLearningApp.model

import android.net.Uri
import com.github.HumanLearning2021.HumanLearningApp.R
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DummyDatabaseServiceTest {

    private val fork = DummyCategory("Fork", "Fork")
    private val knife = DummyCategory("Knife", "Knife")
    private val spoon = DummyCategory("Spoon", "Spoon")
    private val table = DummyCategory("Table", "Table")

    private val forkUri = Uri.parse("android.resource://com.github.HumanLearning2021.HumanLearningApp/"+ R.drawable.fork)
    private val knifeUri = Uri.parse("android.resource://com.github.HumanLearning2021.HumanLearningApp/"+ R.drawable.knife)
    private val spoonUri = Uri.parse("android.resource://com.github.HumanLearning2021.HumanLearningApp/"+ R.drawable.spoon)

    private val forkPic = DummyCategorizedPicture(fork, forkUri)
    private val knifePic = DummyCategorizedPicture(knife, knifeUri)
    private val spoonPic = DummyCategorizedPicture(spoon, spoonUri)

    @ExperimentalCoroutinesApi
    @Test
    fun getForkWorks() = runBlockingTest {
        val dummyDatabaseService = DummyDatabaseService()
        val actual = dummyDatabaseService.getPicture(fork)
        val expected = forkPic
        assertEquals(actual, expected)
    }

    @ExperimentalCoroutinesApi
    @Test(expected = IllegalArgumentException::class)
    fun getPictureCategoryNotPresentThrows() = runBlockingTest {
        DummyDatabaseService().getPicture(DummyCategory("Plate", "Plate"))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getPictureCategoryEmpty() = runBlockingTest {
        val dummyDatabaseService = DummyDatabaseService()
        assertThat(
            dummyDatabaseService.getPicture(dummyDatabaseService.putCategory("Plate")), equalTo(
                null
            )
        )
    }


    @ExperimentalCoroutinesApi
    @Test
    fun putAndThenGetWorks() = runBlockingTest {
        val dummyDatabaseService = DummyDatabaseService()
        dummyDatabaseService.putCategory("Table")
        dummyDatabaseService.putPicture(forkUri, table)

        assertThat(
            dummyDatabaseService.getPicture(table),
            equalTo(DummyCategorizedPicture(table, forkUri))
        )

    }

    @ExperimentalCoroutinesApi
    @Test(expected = IllegalArgumentException::class)
    fun putPictureCategoryNotPresentThrows() = runBlockingTest {
        val dummyDatabaseService = DummyDatabaseService()
        dummyDatabaseService.putPicture(forkUri, table)
    }
    
    @ExperimentalCoroutinesApi
    @Test
    fun getCategoryPresent() = runBlockingTest {
        val dummyDatabaseService = DummyDatabaseService()
        dummyDatabaseService.putCategory("Table")
        assertThat(dummyDatabaseService.getCategory("Table"), equalTo(DummyCategory("Table","Table")))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getCategoryNotPresent() = runBlockingTest {
        val dummyDatabaseService = DummyDatabaseService()
        assertThat(dummyDatabaseService.getCategory("Table"), equalTo(null))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun putCategoryNotPresent() = runBlockingTest {
        val dummyDatabaseService = DummyDatabaseService()
        assertThat(dummyDatabaseService.putCategory("Table"), equalTo(DummyCategory("Table", "Table")))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun putCategoryAlreadyPresentChangesNothing() = runBlockingTest {
        val dummyDatabaseService = DummyDatabaseService()
        dummyDatabaseService.putCategory("Table")
        assertThat(dummyDatabaseService.putCategory("Table"), equalTo(DummyCategory("Table", "Table")))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getCategoriesWorks() = runBlockingTest {
        val dummyDatabaseService = DummyDatabaseService()
        dummyDatabaseService.putCategory("Table")
        assertThat(
            dummyDatabaseService.getCategories(),
            equalTo(setOf(fork, spoon, knife, table))
        )
    }

    @ExperimentalCoroutinesApi
    @Test
    fun putDatasetWorks() = runBlockingTest {
        val dummyDatabaseService = DummyDatabaseService()
        assertThat(
            dummyDatabaseService.putDataset("Utensils", setOf(knife, spoon)),
            equalTo(DummyDataset("Utensils","Utensils", setOf(knife, spoon)))
        )
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getDatasetWorks() = runBlockingTest {
        val dummyDatabaseService = DummyDatabaseService()
        dummyDatabaseService.putDataset("Utensils", setOf(knife, spoon))
        assertThat(
            dummyDatabaseService.getDataset("Utensils")!!.categories,
            equalTo(setOf(knife, spoon))
        )
    }

    @ExperimentalCoroutinesApi
    @Test(expected = java.lang.IllegalArgumentException::class)
    fun removeCategoryThrowsExpectedException() = runBlockingTest {
        DummyDatabaseService().removeCategory(table)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun removeCategoryWorks() = runBlockingTest {
        val dummyDatabaseService = DummyDatabaseService()
        dummyDatabaseService.removeCategory(fork)
        assert(!dummyDatabaseService.getCategories().contains(fork))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun removeCategoryAlsoRemovesFromDatasets() = runBlockingTest {
        val dummyDatabaseService = DummyDatabaseService()
        dummyDatabaseService.putCategory("Fork")
        dummyDatabaseService.putCategory("Spoon")
        dummyDatabaseService.putDataset("Utensils", setOf(fork, spoon))
        dummyDatabaseService.removeCategory(fork)
        assert(!dummyDatabaseService.getDataset("Utensils")!!.categories.contains(fork))
    }

    @ExperimentalCoroutinesApi
    @Test(expected = java.lang.IllegalArgumentException::class)
    fun putRepresentativePictureThrowsExpectedException() = runBlockingTest {
        DummyDatabaseService().putRepresentativePicture(Uri.EMPTY, table)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun putRepresentativePictureWorks() = runBlockingTest {
        val dummyDatabaseService = DummyDatabaseService()
        val table = dummyDatabaseService.putCategory("Table")
        assert(dummyDatabaseService.getRepresentativePicture("Table") == null)
        dummyDatabaseService.putRepresentativePicture(Uri.EMPTY, table)
        assert(dummyDatabaseService.getRepresentativePicture("Table") == DummyCategorizedPicture(table, Uri.EMPTY))
    }

    @ExperimentalCoroutinesApi
    @Test(expected = IllegalArgumentException::class)
    fun deleteDatasetThrowsIllegalArgumentException() = runBlockingTest {
        val dummyDatabaseService = DummyDatabaseService()
        dummyDatabaseService.deleteDataset("Fork")
    }

    @ExperimentalCoroutinesApi
    @Test
    fun deleteDatasetWorks() = runBlockingTest {
        val dummyDatabaseService = DummyDatabaseService()
        dummyDatabaseService.putDataset("Fork", setOf(fork, spoon))
        dummyDatabaseService.deleteDataset("Fork")
        assert(dummyDatabaseService.getDataset("Fork") == null)
    }

    @ExperimentalCoroutinesApi
    @Test(expected = IllegalArgumentException::class)
    fun getAllPicturesThrowsIllegalArgumentException() = runBlockingTest {
        val dummyDatabaseService = DummyDatabaseService()
        dummyDatabaseService.getAllPictures(table)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getAllPicturesGetsAllPictures() = runBlockingTest {
        val dummyDatabaseService = DummyDatabaseService()
        dummyDatabaseService.putCategory("Fork")
        dummyDatabaseService.putPicture(forkUri, fork)
        dummyDatabaseService.putPicture(spoonUri, fork)
        val res = dummyDatabaseService.getAllPictures(fork)
        assert(res.containsAll(setOf(DummyCategorizedPicture(fork, forkUri), DummyCategorizedPicture(fork, spoonUri))))
    }

    @ExperimentalCoroutinesApi
    @Test(expected = IllegalArgumentException::class)
    fun removePictureThrowsIllegalArgumentException() = runBlockingTest {
        DummyDatabaseService().removePicture(DummyCategorizedPicture(DummyCategory("Table", "Table"), Uri.EMPTY))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun removePictureRemovesPicture() = runBlockingTest {
        val dummyDatabaseService = DummyDatabaseService()
        dummyDatabaseService.removePicture(forkPic)
        assert(dummyDatabaseService.getAllPictures(fork).isEmpty())
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getDatasetReturnsNullIfThereIsNone() = runBlockingTest {
        val dummyDatabaseService = DummyDatabaseService()
        assert(dummyDatabaseService.getDataset("Fork") == null)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getDatasetsWorks() = runBlockingTest {
        val dummyDatabaseService = DummyDatabaseService()
        assert(dummyDatabaseService.getDatasets().contains(DummyDataset("kitchen utensils", "kitchen utensils", dummyDatabaseService.getCategories())))
    }

    @ExperimentalCoroutinesApi
    @Test(expected = java.lang.IllegalArgumentException::class)
    fun removeCategoryFromDatasetThrowsIllegalArgumentExceptionIfCategoryNotInDb() = runBlockingTest {
        val dummyDatabaseService = DummyDatabaseService()
        val name = "Utensils"
        val fork = DummyCategory("Fork", "Fork")
        val dataset = DummyDataset(name, name, setOf())
        dummyDatabaseService.removeCategoryFromDataset(dataset, fork)
    }

    @ExperimentalCoroutinesApi
    @Test(expected = java.lang.IllegalArgumentException::class)
    fun removeCategoryFromDatasetThrowsIllegalArgumentExceptionIfDatasetNotInDb() = runBlockingTest {
        val dummyDatabaseService = DummyDatabaseService()
        dummyDatabaseService.removeCategoryFromDataset(DummyDataset("some_id", "some_name", setOf()), fork)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun removeCategoryFromDatasetWorks() = runBlockingTest {
        val dummyDatabaseService = DummyDatabaseService()
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
        val dummyDatabaseService = DummyDatabaseService()
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
        val dummyDatabaseService = DummyDatabaseService()
        val fork = DummyCategory("Fork", "Fork")
        val knife = DummyCategory("Knife", "Knife")
        val name = "Utensils"
        val dataset = dummyDatabaseService.putDataset(name, setOf(fork, knife))
        val newCat = DummyCategory("Spoon", "Spoon")
        require(dataset.categories.containsAll(setOf(fork, knife)))
        require(dataset.categories.size == 2)
        val newDataset = dummyDatabaseService.addCategoryToDataset(dataset, newCat)
        assert(newDataset.categories.containsAll(setOf(fork, knife, newCat)))
    }

    @ExperimentalCoroutinesApi
    @Test(expected = java.lang.IllegalArgumentException::class)
    fun addCategoryToDatasetThrowsIfCategoryNotInDatabase() = runBlockingTest {
        val dummyDatabaseService = DummyDatabaseService()
        val fork = DummyCategory("Fork", "Fork")
        val knife = DummyCategory("Knife", "Knife")
        val name = "Utensils"
        val dataset = dummyDatabaseService.putDataset(name, setOf(fork, knife))
        dummyDatabaseService.addCategoryToDataset(dataset, table)
    }

    @ExperimentalCoroutinesApi
    @Test(expected = java.lang.IllegalArgumentException::class)
    fun addCategoryToDatasetThrowsIfDatasetNotInDatabase() = runBlockingTest {
        val dummyDatabaseService = DummyDatabaseService()
        dummyDatabaseService.addCategoryToDataset(DummyDataset("some_id", "some_name", setOf()), spoon)
    }
}

