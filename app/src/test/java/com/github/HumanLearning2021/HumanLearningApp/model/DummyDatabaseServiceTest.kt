package com.github.HumanLearning2021.HumanLearningApp.model

import android.net.Uri
import com.github.HumanLearning2021.HumanLearningApp.R
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DummyDatabaseServiceTest {


    private val fork = DummyCategory("Fork", "Fork",null)
    private val knife = DummyCategory("Knife", "Knife",null)
    private val spoon = DummyCategory("Spoon", "Spoon",null)
    private val table = DummyCategory("Table", "Table",null)


    private val forkUri = Uri.parse("android.resource://com.github.HumanLearning2021.HumanLearningApp/"+ R.drawable.fork)
    private val knifeUri = Uri.parse("android.resource://com.github.HumanLearning2021.HumanLearningApp/"+ R.drawable.knife)
    private val spoonUri = Uri.parse("android.resource://com.github.HumanLearning2021.HumanLearningApp/"+ R.drawable.spoon)

    private val forkPic = DummyCategorizedPicture(fork, forkUri)
    private val knifePic = DummyCategorizedPicture(knife, knifeUri)
    private val spoonPic = DummyCategorizedPicture(spoon, spoonUri)

    private val forkRepPic = DummyCategorizedPicture(fork, Uri.parse("android.resource://com.github.HumanLearning2021.HumanLearningApp/"+R.drawable.fork_rep))
    private val knifeRepPic = DummyCategorizedPicture(knife, Uri.parse("android.resource://com.github.HumanLearning2021.HumanLearningApp/"+R.drawable.knife_rep))
    private val spoonRepPic = DummyCategorizedPicture(spoon, Uri.parse("android.resource://com.github.HumanLearning2021.HumanLearningApp/"+R.drawable.spoon_rep))


    @Before
    fun init() {
        fork.representativePicture = forkRepPic
        knife.representativePicture = knifeRepPic
        spoon.representativePicture = spoonRepPic
    }

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
        DummyDatabaseService().getPicture(DummyCategory("Plate", "Plate",null))
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
        val table = dummyDatabaseService.putCategory("Table")
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
        val table = dummyDatabaseService.putCategory("Table")
        dummyDatabaseService.removeCategory(table)
        dummyDatabaseService.putPicture(forkUri, table)
    }
    
    @ExperimentalCoroutinesApi
    @Test
    fun getCategoryPresent() = runBlockingTest {
        val dummyDatabaseService = DummyDatabaseService()
        dummyDatabaseService.putCategory("Table")
        assertThat(dummyDatabaseService.getCategory("Table"), equalTo(DummyCategory("Table","Table", null)))
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
        assertThat(dummyDatabaseService.putCategory("Table"), equalTo(DummyCategory("Table", "Table",null)))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun putCategoryAlreadyPresentChangesNothing() = runBlockingTest {
        val dummyDatabaseService = DummyDatabaseService()
        dummyDatabaseService.putCategory("Table")
        assertThat(dummyDatabaseService.putCategory("Table"), equalTo(DummyCategory("Table", "Table",null)))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getCategoriesWorks() = runBlockingTest {
        val dummyDatabaseService = DummyDatabaseService()
        val table = dummyDatabaseService.putCategory("Table")
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
        val dummyDatabaseService = DummyDatabaseService()
        val table = dummyDatabaseService.putCategory("Table")
        dummyDatabaseService.removeCategory(table)
        dummyDatabaseService.removeCategory(table)
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
        dummyDatabaseService.putCategory("Table")
        val table = dummyDatabaseService.getCategory("Table")
        assert(table!!.representativePicture == null)
        dummyDatabaseService.putRepresentativePicture(Uri.EMPTY, table)
        assert(dummyDatabaseService.getCategory("Table")!!.representativePicture!! == DummyCategorizedPicture(table, Uri.EMPTY))
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
        DummyDatabaseService().removePicture(DummyCategorizedPicture(DummyCategory("Table", "Table",null), Uri.EMPTY))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun removePictureRemovesPicture() = runBlockingTest {
        val dummyDatabaseService = DummyDatabaseService()
        val forkCat = dummyDatabaseService.getCategory("Fork")
        val forkPic = dummyDatabaseService.getPicture(forkCat!!)
        dummyDatabaseService.removePicture(forkPic!!)
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
}

