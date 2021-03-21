package com.github.HumanLearning2021.HumanLearningApp.model

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito


class DummyDataSetInterfaceTest {
    val dummyDatasetInterface1 = DummyDatasetInterface()
    val dummyDatasetInterface2 = DummyDatasetInterface()


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
        val actual = dummyDatasetInterface1.getPicture(fork)
        val expected = forkPic
        assertEquals(actual, expected)
    }

    @ExperimentalCoroutinesApi
    @Test(expected = IllegalArgumentException::class)
    fun getPictureCategoryNotPresentThrows() = runBlockingTest {
        DummyDatasetInterface().getPicture(DummyCategory("Plate"))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getPictureCategoryEmpty() = runBlockingTest {
        assertThat(
            dummyDatasetInterface1.getPicture(dummyDatasetInterface1.putCategory("Plate")), equalTo(
                null
            )
        )
    }


    @ExperimentalCoroutinesApi
    @Test
    fun putAndThenGetWorks() = runBlockingTest {

        dummyDatasetInterface1.putCategory("Table")
        dummyDatasetInterface1.putPicture(dummyUri, table)

        assertThat(
            dummyDatasetInterface1.getPicture(table),
            equalTo(DummyCategorizedPicture(table))
        )

    }

    @ExperimentalCoroutinesApi
    @Test(expected = IllegalArgumentException::class)
    fun putPictureCategoryNotPresentThrows() = runBlockingTest {
        dummyDatasetInterface1.putPicture(dummyUri, table)
    }
    
    @ExperimentalCoroutinesApi
    @Test
    fun getCategoryPresent() = runBlockingTest {
        dummyDatasetInterface2.putCategory("Table")
        assertThat(dummyDatasetInterface2.getCategory("Table"), equalTo(DummyCategory("Table")))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getCategoryNotPresent() = runBlockingTest {
        assertThat(dummyDatasetInterface2.getCategory("Table"), equalTo(null))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun putCategoryNotPresent() = runBlockingTest {
        assertThat(dummyDatasetInterface1.putCategory("Table"), equalTo(DummyCategory("Table")))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun putCategoryAlreadyPresentChangesNothing() = runBlockingTest {
        dummyDatasetInterface1.putCategory("Table")
        assertThat(dummyDatasetInterface1.putCategory("Table"), equalTo(DummyCategory("Table")))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getCategoriesWorks() = runBlockingTest {
        dummyDatasetInterface1.putCategory("Table")
        assertThat(
            dummyDatasetInterface1.getCategories(),
            equalTo(setOf(fork, spoon, knife, table))
        )
    }
}

