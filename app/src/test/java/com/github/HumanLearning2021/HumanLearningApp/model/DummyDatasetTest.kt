package com.github.HumanLearning2021.HumanLearningApp.model

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

class DummyDatasetTest {

    @Test
    fun datasetCreatesCorrectly() {
<<<<<<< HEAD
        val fork = DummyCategory("Fork", null)
        val knife = DummyCategory("Knife", null)
        val spoon = DummyCategory("Spoon", null)
        val name = "Utensils"

        val dataset = DummyDataset(name, mutableSetOf(fork, knife, spoon))
=======
        val fork = DummyCategory("Fork")
        val knife = DummyCategory("Knife")
        val spoon = DummyCategory("Spoon")
        val name = "Utensils"

        val dataset = DummyDataset(name, setOf(fork, knife, spoon))
>>>>>>> main

        assert(dataset.name == name)
        assert(dataset.categories.containsAll(setOf(fork, knife, spoon)))
    }
}