package com.github.HumanLearning2021.HumanLearningApp.model

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito


class DummyDatabaseServiceTest {
    val dummyDatabaseService1 = DummyDatabaseService()
    val dummyDatabseService2 = DummyDatabaseService()


    private val fork = DummyCategory("Fork")
    private val knife = DummyCategory("Knife")
    private val spoon = DummyCategory("Spoon")
    private val table = DummyCategory("Table")

    val categories: Set<Category> = mutableSetOf(fork, knife, spoon)

    private val forkPic = DummyCategorizedPicture(fork)
    private val knifePic = DummyCategorizedPicture(knife)
    private val spoonPic = DummyCategorizedPicture(spoon)
    private val tablePic = DummyCategorizedPicture(table)

    val dummyUri = Mockito.mock(android.net.Uri::class.java)

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
        DummyDatabaseService().getPicture(DummyCategory("Plate"))
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
        dummyDatabaseService1.putPicture(dummyUri, table)

        assertThat(
            dummyDatabaseService1.getPicture(table),
            equalTo(DummyCategorizedPicture(table))
        )

    }

    @ExperimentalCoroutinesApi
    @Test(expected = IllegalArgumentException::class)
    fun putPictureCategoryNotPresentThrows() = runBlockingTest {
        dummyDatabaseService1.putPicture(dummyUri, table)
    }
    
    @ExperimentalCoroutinesApi
    @Test
    fun getCategoryPresent() = runBlockingTest {
        dummyDatabseService2.putCategory("Table")
        assertThat(dummyDatabseService2.getCategory("Table"), equalTo(DummyCategory("Table")))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getCategoryNotPresent() = runBlockingTest {
        assertThat(dummyDatabseService2.getCategory("Table"), equalTo(null))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun putCategoryNotPresent() = runBlockingTest {
        assertThat(dummyDatabaseService1.putCategory("Table"), equalTo(DummyCategory("Table")))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun putCategoryAlreadyPresentChangesNothing() = runBlockingTest {
        dummyDatabaseService1.putCategory("Table")
        assertThat(dummyDatabaseService1.putCategory("Table"), equalTo(DummyCategory("Table")))
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
            equalTo(DummyDataset("Utensils", setOf(knife, spoon)))
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

