package com.github.HumanLearning2021.HumanLearningApp.model

import android.net.Uri
import com.github.HumanLearning2021.HumanLearningApp.R
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DummyDatabaseManagementTest {

    var testDatabaseManagement = DummyDatabaseManagement(DummyDatabaseService())

    @Before
    fun bef() {
        testDatabaseManagement = DummyDatabaseManagement(DummyDatabaseService())
    }

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

    @ExperimentalCoroutinesApi
    @Test(expected = IllegalArgumentException::class)
    fun getPictureThrowsIllegalArgumentException() = runBlockingTest {
        testDatabaseManagement.getPicture(table)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getPictureWorks() = runBlockingTest {
        assert(testDatabaseManagement.getPicture(fork)!! == forkPic)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun putAndThenGetWorks() = runBlockingTest {
        val newCat = testDatabaseManagement.putCategory("Table")
        val newPic = testDatabaseManagement.putPicture(knifeUri, newCat)
        assert(testDatabaseManagement.getPicture(newCat) != null)
    }

    @ExperimentalCoroutinesApi
    @Test(expected = IllegalArgumentException::class)
    fun putPictureThrowsIllegalArgumentException() = runBlockingTest {
        testDatabaseManagement.putPicture(Uri.EMPTY, table)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getCategoryByIdPresent() = runBlockingTest {
        assert(testDatabaseManagement.getCategoryById("Fork") != null)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getCategoryByIdNotPresent() = runBlockingTest {
        assert(testDatabaseManagement.getCategoryById("Table") == null)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getCategoryByNamePresent() = runBlockingTest {
        assert(testDatabaseManagement.getCategoryByName("Fork").contains(fork))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getCategoryByNameNotPresent() = runBlockingTest {
        assert(testDatabaseManagement.getCategoryByName("Table").isEmpty())
    }

    @ExperimentalCoroutinesApi
    @Test
    fun putCategoryNotPresent() = runBlockingTest {
        val cat = testDatabaseManagement.putCategory("Table")
        assert(testDatabaseManagement.getCategories().contains(cat))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun putCategoryAlreadyPresentChangesNothing() = runBlockingTest {
        assert(testDatabaseManagement.putCategory("Fork") == fork)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getCategoriesWorks() = runBlockingTest {
        assert(testDatabaseManagement.getCategories().containsAll(setOf(fork, spoon, knife)))
    }

    @ExperimentalCoroutinesApi
    @Test(expected = java.lang.IllegalArgumentException::class)
    fun getAllPicturesThrowsException() = runBlockingTest {
        testDatabaseManagement.getAllPictures(table)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getAllPicturesWorks() = runBlockingTest {
        assert(testDatabaseManagement.getAllPictures(fork).contains(forkPic))
        assert(testDatabaseManagement.getAllPictures(fork).size == 1)
    }

    @ExperimentalCoroutinesApi
    @Test(expected = java.lang.IllegalArgumentException::class)
    fun removeCategoryThrowsExpectedException() = runBlockingTest {
        testDatabaseManagement.removeCategory(table)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun removeCategoryWorks() = runBlockingTest {
        testDatabaseManagement.removeCategory(fork)
        assert(!testDatabaseManagement.getCategories().contains(fork))
    }

    @ExperimentalCoroutinesApi
    @Test(expected = IllegalArgumentException::class)
    fun removePictureThrowsIllegalArgumentException() = runBlockingTest {
        testDatabaseManagement.removePicture(DummyCategorizedPicture(table, Uri.EMPTY))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun removePictureRemovesPicture() = runBlockingTest {
        testDatabaseManagement.removePicture(forkPic)
        assert(testDatabaseManagement.getAllPictures(fork).isEmpty())
    }

    @ExperimentalCoroutinesApi
    @Test
    fun putDatasetWorks() = runBlockingTest {
        val newDs = testDatabaseManagement.putDataset("NewDs", setOf())
        assert(testDatabaseManagement.getDatasetById(newDs.id) == newDs)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getDatasetByIdWorks() = runBlockingTest {
        val ds = testDatabaseManagement.putDataset("ds", setOf())
        assert(testDatabaseManagement.getDatasetById(ds.id) == ds)
    }

    @ExperimentalCoroutinesApi
    fun getDatasetByNameWorks() = runBlockingTest {
        val ds = testDatabaseManagement.putDataset("ds", setOf())
        val res = testDatabaseManagement.getDatasetByName(ds.name)
        assert(res.contains(ds) && res.size == 1)
    }

    @ExperimentalCoroutinesApi
    @Test(expected = IllegalArgumentException::class)
    fun deleteDatasetThrowsIllegalArgumentException() = runBlockingTest {
        testDatabaseManagement.deleteDataset("someDsId")
    }

    @ExperimentalCoroutinesApi
    @Test
    fun deleteDatasetWorks() = runBlockingTest {
        val dummyDatabaseService = DummyDatabaseService()
        testDatabaseManagement.deleteDataset("kitchen utensils")
        assert(!dummyDatabaseService.getDatasets().contains("kitchen utensils"))
    }

    @ExperimentalCoroutinesApi
    @Test(expected = java.lang.IllegalArgumentException::class)
    fun putRepresentativePictureThrowsExpectedException() = runBlockingTest {
        testDatabaseManagement.putRepresentativePicture(Uri.EMPTY, table)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun putRepresentativePictureWorks() = runBlockingTest {
        testDatabaseManagement.putRepresentativePicture(Uri.EMPTY, fork)
        assert(testDatabaseManagement.getCategoryById(fork.id)!!.representativePicture != null)
    }

    @ExperimentalCoroutinesApi
    @Test(expected = java.lang.IllegalArgumentException::class)
    fun putRepresentativePictureOverloadThrowsExpectedException() = runBlockingTest {
        testDatabaseManagement.putRepresentativePicture(DummyCategorizedPicture(table, Uri.EMPTY))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun putRepresentativePictureOverloadWorks() = runBlockingTest {
        testDatabaseManagement.putRepresentativePicture(DummyCategorizedPicture(fork, Uri.EMPTY))
        assert(testDatabaseManagement.getCategoryById(fork.id)!!.representativePicture != null)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getDatasetsWorks() = runBlockingTest {
        assert(testDatabaseManagement.getDatasets().size == 1)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getDatasetNamesWorks() = runBlockingTest {
        assert(testDatabaseManagement.getDatasetNames().contains("kitchen utensils"))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getDatasetIdsWorks() = runBlockingTest {
        assert(testDatabaseManagement.getDatasetIds().contains("kitchen utensils"))
    }
}