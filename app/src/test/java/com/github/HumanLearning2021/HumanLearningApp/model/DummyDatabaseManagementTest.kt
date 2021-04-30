package com.github.HumanLearning2021.HumanLearningApp.model

import android.net.Uri
import com.github.HumanLearning2021.HumanLearningApp.R
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.hasItems
import org.hamcrest.MatcherAssert.assertThat
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

    private val fork = DummyCategory("Fork", "Fork")
    private val knife = DummyCategory("Knife", "Knife")
    private val spoon = DummyCategory("Spoon", "Spoon")
    private val table = DummyCategory("Table", "Table")

    private val forkUri = Uri.parse("android.resource://com.github.HumanLearning2021.HumanLearningApp/"+ R.drawable.fork)
    private val knifeUri = Uri.parse("android.resource://com.github.HumanLearning2021.HumanLearningApp/"+ R.drawable.knife)
    private val spoonUri = Uri.parse("android.resource://com.github.HumanLearning2021.HumanLearningApp/"+ R.drawable.spoon)

    private val forkPic = DummyCategorizedPicture("forkpicid", fork, forkUri)
    private val knifePic = DummyCategorizedPicture("knifepicid", knife, knifeUri)
    private val spoonPic = DummyCategorizedPicture("spoonpicid", spoon, spoonUri)

    @ExperimentalCoroutinesApi
    @Suppress("DEPRECATION")
    @Test(expected = IllegalArgumentException::class)
    fun getPictureThrowsIllegalArgumentException() = runBlockingTest {
        testDatabaseManagement.getPicture(table)
    }

    @ExperimentalCoroutinesApi
    @Suppress("DEPRECATION")
    @Test
    fun getPictureWorks() = runBlockingTest {
        assert(testDatabaseManagement.getPicture(fork)!! == forkPic)
    }

    @ExperimentalCoroutinesApi
    @Suppress("DEPRECATION")
    @Test
    fun putAndThenGetWorks() = runBlockingTest {
        val newCat = testDatabaseManagement.putCategory("Table")
        testDatabaseManagement.putPicture(knifeUri, newCat)
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
    @Test
    fun removeNonexistentCategory() = runBlockingTest {
        testDatabaseManagement.removeCategory(table)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun removeCategoryWorks() = runBlockingTest {
        testDatabaseManagement.removeCategory(fork)
        assert(!testDatabaseManagement.getCategories().contains(fork))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun removeNonexistentPicture() = runBlockingTest {
        testDatabaseManagement.removePicture(DummyCategorizedPicture("tableid", table, Uri.EMPTY))
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
    @Test
    fun getDatasetByNameWorks() = runBlockingTest {
        val ds = testDatabaseManagement.putDataset("ds", setOf())
        val res = testDatabaseManagement.getDatasetByName(ds.name)
        assert(res.contains(ds) && res.size == 1)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun deleteNonexistentDataset() = runBlockingTest {
        testDatabaseManagement.deleteDataset("someDsId")
    }

    @ExperimentalCoroutinesApi
    @Test
    fun deleteDatasetWorks() = runBlockingTest {
        val id = "kitchen utensils"
        testDatabaseManagement.deleteDataset(id)
        assert(!testDatabaseManagement.getDatasetIds().contains(id))
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
        assert(testDatabaseManagement.getRepresentativePicture(fork.id) != null)
    }

    @ExperimentalCoroutinesApi
    @Test(expected = java.lang.IllegalArgumentException::class)
    fun putRepresentativePictureOverloadThrowsExpectedException() = runBlockingTest {
        testDatabaseManagement.putRepresentativePicture(DummyCategorizedPicture("tableid", table, Uri.EMPTY))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun putRepresentativePictureOverloadWorks() = runBlockingTest {
        testDatabaseManagement.putRepresentativePicture(DummyCategorizedPicture("forkid", fork, Uri.EMPTY))
        assert(testDatabaseManagement.getRepresentativePicture(fork.id) != null)
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

    @ExperimentalCoroutinesApi
    @Test(expected = java.lang.IllegalArgumentException::class)
    fun removeCategoryFromDatasetThrowsIllegalArgumentExceptionIfCategoryNotInDb() = runBlockingTest {
        val name = "Utensils"
        val fork = DummyCategory("Fork", "Fork")
        val dataset = DummyDataset(name, name, setOf())
        testDatabaseManagement.removeCategoryFromDataset(dataset, fork)
    }

    @ExperimentalCoroutinesApi
    @Test(expected = java.lang.IllegalArgumentException::class)
    fun removeCategoryFromDatasetThrowsIllegalArgumentExceptionIfDatasetNotInDb() = runBlockingTest {
        testDatabaseManagement.removeCategoryFromDataset(DummyDataset("some_id", "some_name", setOf()), fork)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun removeCategoryFromDatasetWorks() = runBlockingTest {
        val fork = DummyCategory("Fork", "Fork")
        val knife = DummyCategory("Knife", "Knife")
        val spoon = DummyCategory("Spoon", "Spoon")
        val name = "Utensils"
        val dataset = testDatabaseManagement.putDataset(name, setOf(fork, knife, spoon))
        val newDataset = testDatabaseManagement.removeCategoryFromDataset(dataset, fork)
        assert(newDataset.categories.containsAll(setOf(knife, spoon)))
        assert(!newDataset.categories.contains(fork))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun addCategoryToDatasetWorks() = runBlockingTest {
        val fork = DummyCategory("Fork", "Fork")
        val knife = DummyCategory("Knife", "Knife")
        val name = "Utensils"
        val dataset = testDatabaseManagement.putDataset(name, setOf(fork, knife))
        val newCat = DummyCategory("Spoon", "Spoon")
        require(dataset.categories.containsAll(setOf(fork, knife)))
        require(dataset.categories.size == 2)
        val newDataset = testDatabaseManagement.addCategoryToDataset(dataset, newCat)
        assert(newDataset.categories.containsAll(setOf(fork, knife, newCat)))
    }

    @ExperimentalCoroutinesApi
    @Test(expected = java.lang.IllegalArgumentException::class)
    fun addCategoryToDatasetThrowsIfCategoryNotInDatabase() = runBlockingTest {
        val fork = DummyCategory("Fork", "Fork")
        val knife = DummyCategory("Knife", "Knife")
        val name = "Utensils"
        val dataset = testDatabaseManagement.putDataset(name, setOf(fork, knife))
        testDatabaseManagement.addCategoryToDataset(dataset, table)
    }

    @ExperimentalCoroutinesApi
    @Test(expected = java.lang.IllegalArgumentException::class)
    fun addCategoryToDatasetThrowsIfDatasetNotInDatabase() = runBlockingTest {
        testDatabaseManagement.addCategoryToDataset(DummyDataset("some_id", "some_name", setOf()), spoon)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun editDatasetNameWorks() = runBlockingTest {
        val fork = DummyCategory("Fork", "Fork")
        val knife = DummyCategory("Knife", "Knife")
        val spoon = DummyCategory("Spoon", "Spoon")
        val name = "Utensils"
        val newName = "NoLongerUtensils"
        val dataset = testDatabaseManagement.putDataset(name, setOf(fork, knife, spoon))
        val newDataset = testDatabaseManagement.editDatasetName(dataset, newName)
        assert(newDataset.categories.containsAll(dataset.categories))
        assert(newDataset.name == newName)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getPictureIds() = runBlockingTest {
        assertThat(testDatabaseManagement.getPictureIds(fork), hasItems("forkpicid"))
    }

    @ExperimentalCoroutinesApi
    @Test(expected = java.lang.IllegalArgumentException::class)
    fun getPictureIdsThrows() = runBlockingTest {
        testDatabaseManagement.getPictureIds(table)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getPictureById() = runBlockingTest {
        assertThat(testDatabaseManagement.getPicture("forkpicid")!!.category, equalTo(fork))
    }
}
