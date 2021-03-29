package com.github.HumanLearning2021.HumanLearningApp.model

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

class DummyDatasetTest {

    @Test
    fun datasetCreatesCorrectly() {
        val fork = DummyCategory("Fork", null)
        val knife = DummyCategory("Knife", null)
        val spoon = DummyCategory("Spoon", null)
        val name = "Utensils"

        val dataset = DummyDataset(name, setOf(fork, knife, spoon))

        assert(dataset.name == name)
        assert(dataset.categories.containsAll(setOf(fork, knife, spoon)))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun removeCategoryThrowsExpectedException() = runBlockingTest {
        val fork = DummyCategory("Fork", null)
        val knife = DummyCategory("Knife", null)
        val name = "Utensils"

        val dataset = DummyDataset(name, setOf(fork))

        try {
            dataset.removeCategory(knife)
        } catch (e: IllegalArgumentException) {
            assert(true)
        }

    }

    @ExperimentalCoroutinesApi
    @Test
    fun removeCategoryReturnsDatasetWithCategoryRemoved() = runBlockingTest {
        val fork = DummyCategory("Fork", null)
        val knife = DummyCategory("Knife", null)
        val spoon = DummyCategory("Spoon", null)
        val name = "Utensils"

        val dataset = DummyDataset(name, setOf(fork, knife, spoon))

        val newDataset = dataset.removeCategory(fork)

        assert(newDataset.name == dataset.name)
        assert(newDataset.categories.containsAll(setOf(knife, spoon)))
        assert(!newDataset.categories.contains(fork))
    }
}