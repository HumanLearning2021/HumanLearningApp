package com.github.HumanLearning2021.HumanLearningApp.model

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

class DummyDatasetTest {

    @Test
    fun datasetCreatesCorrectly() {
        val fork = DummyCategory("Fork", "Fork",null)
        val knife = DummyCategory("Knife", "Knife", null)
        val spoon = DummyCategory("Spoon", "Spoon",null)
        val name = "Utensils"

        val dataset = DummyDataset(name, name, setOf(fork, knife, spoon))

        assert(dataset.name == name)
        assert(dataset.categories.containsAll(setOf(fork, knife, spoon)))
    }
}