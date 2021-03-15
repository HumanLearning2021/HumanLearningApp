package com.github.HumanLearning2021.HumanLearningApp.Model

import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

import org.junit.Assert.*

class DummyDataSetInterfaceTest {
    val dummyDataSetInterface = DummyDataSetInterface()

    private val fork = DummyCategory("Fork")
    private val knife = DummyCategory("Knife")
    private val spoon = DummyCategory("Spoon")

    val categories: Set<Category> = setOf(fork, knife, spoon)

    private val forkPic = DummyCategorizedPicture(fork)
    private val knifePic = DummyCategorizedPicture(knife)
    private val spoonPic = DummyCategorizedPicture(spoon)

    @Test
    fun getCategories() {
        assertEquals(dummyDataSetInterface.categories, setOf(fork, knife, spoon))
    }

    @Test
    fun getPicture() = runBlockingTest {
        val actual = dummyDataSetInterface.getPicture(fork)
        val expected = forkPic
        assertEquals(actual, expected)
    }
}