package com.github.HumanLearning2021.HumanLearningApp.model

import android.net.Uri
import com.github.HumanLearning2021.HumanLearningApp.R
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DummyDatabaseManagementTest {

    private val testDatabaseManagement = DummyDatabaseManagement

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
        val mockTestDatabaseManagement = mock(DummyDatabaseManagement::class.java)
        val newCat = mockTestDatabaseManagement.putCategory("Table")
        val newPic = mockTestDatabaseManagement.putPicture(knifeUri, newCat)
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
        val mockTestDatabaseManagement = mock(DummyDatabaseManagement::class.java)
        val cat = mockTestDatabaseManagement.putCategory("Table")
        assert(mockTestDatabaseManagement.getCategories().contains(cat))
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
    fun getRepresentativePictureThrowsIllegalArgumentException() = runBlockingTest {
        testDatabaseManagement.getRepresentativePicture(table)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getRepresentativePictureWorks() = runBlockingTest {
        assert(testDatabaseManagement.getRepresentativePicture(fork) == null)
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
        val mockedTestDatabaseManagement = mock(DummyDatabaseManagement::class.java)
        mockedTestDatabaseManagement.removeCategory(fork)
        assert(!mockedTestDatabaseManagement.getCategories().contains(fork))
    }

    @ExperimentalCoroutinesApi
    @Test(expected = IllegalArgumentException::class)
    fun removePictureThrowsIllegalArgumentException() = runBlockingTest {
        testDatabaseManagement.removePicture(DummyCategorizedPicture(table, Uri.EMPTY))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun removePictureRemovesPicture() = runBlockingTest {
        val mockedTestDatabaseManagement = mock(DummyDatabaseManagement::class.java)
        mockedTestDatabaseManagement.removePicture(forkPic)
        assert(mockedTestDatabaseManagement.getAllPictures(fork).isEmpty())
    }

    @ExperimentalCoroutinesApi
    @Test
    fun putDatasetWorks() = runBlockingTest {
        val mockedTestDatabaseManagement = mock(DummyDatabaseManagement::class.java)
        val newDs = mockedTestDatabaseManagement.putDataset("NewDs", setOf())
        assert(mockedTestDatabaseManagement.getDatasetById(newDs.id) == newDs)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getDatasetByIdWorks() = runBlockingTest {
        val mockedTestDatabaseManagement = mock(DummyDatabaseManagement::class.java)
        val ds = mockedTestDatabaseManagement.putDataset("ds", setOf())
        assert(testDatabaseManagement.getDatasetById(ds.id) == ds)
    }

    @ExperimentalCoroutinesApi
    fun getDatasetByNameWorks() = runBlockingTest {
        val mockedTestDatabaseManagement = mock(DummyDatabaseManagement::class.java)
        val ds = mockedTestDatabaseManagement.putDataset("ds", setOf())
        assert(testDatabaseManagement.getDatasetById(ds.name) == ds)
    }

    @ExperimentalCoroutinesApi
    @Test(expected = IllegalArgumentException::class)
    fun deleteDatasetThrowsIllegalArgumentException() = runBlockingTest {
        testDatabaseManagement.deleteDataset("someDsId")
    }

    @ExperimentalCoroutinesApi
    @Test
    fun deleteDatasetWorks() = runBlockingTest {
        val mockedTestDatabaseManagement = mock(DummyDatabaseManagement::class.java)
        val ds = mockedTestDatabaseManagement.putDataset("someDs", setOf())
        mockedTestDatabaseManagement.deleteDataset(ds.id)
        assert(!mockedTestDatabaseManagement.getDatasets().contains(ds))
    }

    @ExperimentalCoroutinesApi
    @Test(expected = java.lang.IllegalArgumentException::class)
    fun putRepresentativePictureThrowsExpectedException() = runBlockingTest {
        testDatabaseManagement.putRepresentativePicture(Uri.EMPTY, table)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun putRepresentativePictureWorks() = runBlockingTest {
        val mockedTestDatabaseManagement = mock(DummyDatabaseManagement::class.java)
        mockedTestDatabaseManagement.putRepresentativePicture(Uri.EMPTY, fork)
        assert(mockedTestDatabaseManagement.getRepresentativePicture(fork) != null)
    }
    @ExperimentalCoroutinesApi
    @Test(expected = java.lang.IllegalArgumentException::class)
    fun putRepresentativePictureOverloadThrowsExpectedException() = runBlockingTest {
        testDatabaseManagement.putRepresentativePicture(DummyCategorizedPicture(table, Uri.EMPTY))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun putRepresentativePictureOverloadWorks() = runBlockingTest {
        val mockedTestDatabaseManagement = mock(DummyDatabaseManagement::class.java)
        mockedTestDatabaseManagement.putRepresentativePicture(DummyCategorizedPicture(table, Uri.EMPTY))
        assert(mockedTestDatabaseManagement.getRepresentativePicture(fork) != null)
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