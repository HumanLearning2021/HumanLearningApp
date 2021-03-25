
package com.github.HumanLearning2021.HumanLearningApp.model
import android.net.Uri
import com.github.HumanLearning2021.HumanLearningApp.R
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.mock

class DummyDatabaseServiceTest {
    val dummyDatabaseService1Mock = mock(DummyDatabaseService::class.java)
    val dummyDatabseService2Mock = mock(DummyDatabaseService::class.java)

    private val fork = DummyCategory("Fork", null)
    private val knife = DummyCategory("Knife", null)
    private val spoon = DummyCategory("Spoon", null)
    private val table = DummyCategory("Table", null)

    val categories: Set<Category> = mutableSetOf(fork, knife, spoon)
    val dummyUri = Mockito.mock(android.net.Uri::class.java)

    private val forkPic = DummyCategorizedPicture(fork, dummyUri)
    private val knifePic = DummyCategorizedPicture(knife, dummyUri)
    private val spoonPic = DummyCategorizedPicture(spoon, dummyUri)
    private val tablePic = DummyCategorizedPicture(table, dummyUri)


    @ExperimentalCoroutinesApi
    @Test
    fun getForkWorks() = runBlockingTest {
        val actual = dummyDatabaseService1Mock.getPicture(fork)
        val expected = forkPic
        assertEquals(actual, expected)
    }

    @ExperimentalCoroutinesApi
    @Test(expected = IllegalArgumentException::class)
    fun getPictureCategoryNotPresentThrows() = runBlockingTest {
        DummyDatabaseService().getPicture(DummyCategory("Plate", null))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getPictureCategoryEmpty() = runBlockingTest {
        assertThat(
            dummyDatabaseService1Mock.getPicture(dummyDatabaseService1Mock.putCategory("Plate")), equalTo(
                null
            )
        )
    }


    @ExperimentalCoroutinesApi
    @Test
    fun putAndThenGetWorks() = runBlockingTest {

        dummyDatabaseService1Mock.putCategory("Table")
        dummyDatabaseService1Mock.putPicture(dummyUri, table)

        assertThat(
            dummyDatabaseService1Mock.getPicture(table),
            equalTo(DummyCategorizedPicture(table, dummyUri))
        )

    }

    @ExperimentalCoroutinesApi
    @Test(expected = IllegalArgumentException::class)
    fun putPictureCategoryNotPresentThrows() = runBlockingTest {
        dummyDatabaseService1Mock.putPicture(dummyUri, table)
    }
    
    @ExperimentalCoroutinesApi
    @Test
    fun getCategoryPresent() = runBlockingTest {
        dummyDatabseService2Mock.putCategory("Table")
        assertThat(dummyDatabseService2Mock.getCategory("Table"), equalTo(DummyCategory("Table", null)))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getCategoryNotPresent() = runBlockingTest {
        assertThat(dummyDatabseService2Mock.getCategory("Table"), equalTo(null))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun putCategoryNotPresent() = runBlockingTest {
        assertThat(dummyDatabaseService1Mock.putCategory("Table"), equalTo(DummyCategory("Table", null)))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun putCategoryAlreadyPresentChangesNothing() = runBlockingTest {
        dummyDatabaseService1Mock.putCategory("Table")
        assertThat(dummyDatabaseService1Mock.putCategory("Table"), equalTo(DummyCategory("Table", null)))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getCategoriesWorks() = runBlockingTest {
        dummyDatabaseService1Mock.putCategory("Table")
        assertThat(
            dummyDatabaseService1Mock.getCategories(),
            equalTo(setOf(fork, spoon, knife, table))
        )
    }

    @ExperimentalCoroutinesApi
    @Test
    fun putDatasetWorks() = runBlockingTest {
        assertThat(
            dummyDatabaseService1Mock.putDataset("Utensils", mutableSetOf(knife, spoon)),
            equalTo(DummyDataset("Utensils", mutableSetOf(knife, spoon)))
        )
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getDatasetWorks() = runBlockingTest {
        dummyDatabaseService1Mock.putDataset("Utensils", mutableSetOf(knife, spoon))
        assertThat(
            dummyDatabaseService1Mock.getDataset("Utensils")!!.categories,
            equalTo(setOf(knife, spoon))
        )
    }
}

