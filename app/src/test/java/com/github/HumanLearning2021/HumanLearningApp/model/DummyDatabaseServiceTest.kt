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
    private val dummyDatabaseService1 = DummyDatabaseService()
    private val dummyDatabseService2 = DummyDatabaseService()


    private val fork = DummyCategory("Fork", "Fork",null)
    private val knife = DummyCategory("Knife", "Knife",null)
    private val spoon = DummyCategory("Spoon", "Spoon",null)
    private val table = DummyCategory("Table", "Table",null)

    val categories: Set<Category> = mutableSetOf(fork, knife, spoon)

    private val forkPic = DummyCategorizedPicture(fork, Uri.parse("android.resource://com.github.HumanLearning2021.HumanLearningApp/"+ R.drawable.fork))
//    private val knifePic = DummyCategorizedPicture(knife)
//    private val spoonPic = DummyCategorizedPicture(spoon)
//    private val tablePic = DummyCategorizedPicture(table)

    @ExperimentalCoroutinesApi
    @Test
    fun getForkWorks() = runBlockingTest {
        val actual = dummyDatabaseService1.getPicture(fork)
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
        assertThat(
            dummyDatabaseService1.getPicture(dummyDatabaseService1.putCategory("Plate")), equalTo(
                null
            )
        )
    }


    @ExperimentalCoroutinesApi
    @Test
    fun putAndThenGetWorks() = runBlockingTest {

        dummyDatabaseService1.putCategory("Table")
        dummyDatabaseService1.putPicture(Uri.parse("android.resource://com.github.HumanLearning2021.HumanLearningApp/"+ R.drawable.fork), table)

        assertThat(
            dummyDatabaseService1.getPicture(table),
            equalTo(DummyCategorizedPicture(table, Uri.parse("android.resource://com.github.HumanLearning2021.HumanLearningApp/"+ R.drawable.fork)))
        )

    }

    @ExperimentalCoroutinesApi
    @Test(expected = IllegalArgumentException::class)
    fun putPictureCategoryNotPresentThrows() = runBlockingTest {
        dummyDatabaseService1.putPicture(Uri.parse("android.resource://com.github.HumanLearning2021.HumanLearningApp/"+ R.drawable.fork), table)
    }
    
    @ExperimentalCoroutinesApi
    @Test
    fun getCategoryPresent() = runBlockingTest {
        dummyDatabseService2.putCategory("Table")
        assertThat(dummyDatabseService2.getCategory("Table"), equalTo(DummyCategory("Table", "Table",null)))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getCategoryNotPresent() = runBlockingTest {
        assertThat(dummyDatabseService2.getCategory("Table"), equalTo(null))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun putCategoryNotPresent() = runBlockingTest {
        assertThat(dummyDatabaseService1.putCategory("Table"), equalTo(DummyCategory("Table", "Table",null)))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun putCategoryAlreadyPresentChangesNothing() = runBlockingTest {
        dummyDatabaseService1.putCategory("Table")
        assertThat(dummyDatabaseService1.putCategory("Table"), equalTo(DummyCategory("Table", "Table",null)))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getCategoriesWorks() = runBlockingTest {
        dummyDatabaseService1.putCategory("Table")
        assertThat(
            dummyDatabaseService1.getCategories(),
            equalTo(setOf(fork, spoon, knife, table))
        )
    }

    @ExperimentalCoroutinesApi
    @Test
    fun putDatasetWorks() = runBlockingTest {
        assertThat(
            dummyDatabaseService1.putDataset("Utensils", setOf(knife, spoon)),
            equalTo(DummyDataset("Utensils", "Utensils", setOf(knife, spoon)))
        )
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getDatasetWorks() = runBlockingTest {
        dummyDatabaseService1.putDataset("Utensils", setOf(knife, spoon))
        assertThat(
            dummyDatabaseService1.getDataset("Utensils")!!.categories,
            equalTo(setOf(knife, spoon))
        )
    }
}

