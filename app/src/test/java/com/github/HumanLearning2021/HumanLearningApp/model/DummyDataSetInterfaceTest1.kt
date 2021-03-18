package com.github.HumanLearning2021.HumanLearningApp.model

import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

import org.junit.Assert.*

class DummyDataSetInterfaceTest {
    val dummyDatasetInterface1 = DummyDatasetInterface()
    val dummyDatasetInterface2 = DummyDatasetInterface()


    private val fork = DummyCategory("Fork")
    private val knife = DummyCategory("Knife")
    private val spoon = DummyCategory("Spoon")

    val categories: Set<Category> = setOf(fork, knife, spoon)

    private val forkPic = DummyCategorizedPicture(fork)
    private val knifePic = DummyCategorizedPicture(knife)
    private val spoonPic = DummyCategorizedPicture(spoon)

    @Test
    fun getCategories() {
        assertEquals(dummyDatasetInterface1.categories, setOf(fork, knife, spoon))
    }

    @Test
    fun getPictureWorks() = runBlockingTest {
        val actual = dummyDatasetInterface1.getPicture(fork)
        val expected = forkPic
        assertEquals(actual, expected)
    }

    @Test
    fun getPictureInvalidCategory() = runBlockingTest {
        assert(dummyDatasetInterface1.getPicture(DummyCategory("plate")) == null)
    }
}