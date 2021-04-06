package com.github.HumanLearning2021.HumanLearningApp.model

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import java.lang.IllegalArgumentException

class DummyDatasetTest {

    @Test
    fun datasetCreatesCorrectly() {
        val fork = DummyCategory("Fork", "Fork")
        val knife = DummyCategory("Knife", "Knife")
        val spoon = DummyCategory("Spoon", "Spoon")
        val name = "Utensils"
        val dataset = DummyDataset(name, name, setOf(fork, knife, spoon))

        assert(dataset.name == name)
        assert(dataset.id == name)
        assert(dataset.categories.containsAll(setOf(fork, knife, spoon)))
    }

    @ExperimentalCoroutinesApi
    @Test(expected = IllegalArgumentException::class)
    fun removeCategoryThrowsIllegalArgumentException() = runBlockingTest {
        val name = "Utensils"
        val fork = DummyCategory("Fork", "Fork")
        val dataset = DummyDataset(name, name, setOf())

        dataset.removeCategory(fork)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun removeCategoryWorks() = runBlockingTest {
        val fork = DummyCategory("Fork", "Fork")
        val knife = DummyCategory("Knife", "Knife")
        val spoon = DummyCategory("Spoon", "Spoon")
        val name = "Utensils"
        val dataset = DummyDataset(name, name, setOf(fork, knife, spoon))

        val newDataset = dataset.removeCategory(fork)
        assert(newDataset.categories.containsAll(setOf(knife, spoon)))
        assert(!newDataset.categories.contains(fork))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun editDatasetNameWorks() = runBlockingTest {
        val fork = DummyCategory("Fork", "Fork")
        val knife = DummyCategory("Knife", "Knife")
        val spoon = DummyCategory("Spoon", "Spoon")
        val name = "Utensils"
        val newName = "NoLongerUtensils"
        val dataset = DummyDataset(name, name, setOf(fork, knife, spoon))
        val newDataset = dataset.editDatasetName(newName)
        assert(newDataset.categories.containsAll(dataset.categories))
        assert(newDataset.name == newName)
    }
}