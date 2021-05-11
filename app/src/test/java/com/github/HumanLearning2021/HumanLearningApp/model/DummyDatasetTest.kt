package com.github.HumanLearning2021.HumanLearningApp.model

import org.junit.Test

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
}