package com.github.HumanLearning2021.HumanLearningApp.model

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

class DummyDatasetTest {

    @Test
    fun datasetCreatesCorrectly() {
        val fork = DummyCategory("Fork")
        val knife = DummyCategory("Knife")
        val spoon = DummyCategory("Spoon")
        val name = "Utensils"

        val dataset = DummyDataset(name, setOf(fork, knife, spoon))

        assert(dataset.name == name)
        assert(dataset.categories.containsAll(setOf(fork, knife, spoon)))
    }
}