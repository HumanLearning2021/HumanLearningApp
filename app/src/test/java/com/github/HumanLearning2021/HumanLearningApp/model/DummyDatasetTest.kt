package com.github.HumanLearning2021.HumanLearningApp.model

import org.junit.Test

class DummyDatasetTest {

    @Test
    fun datasetCreatesCorrectly() {
        val fork = Category("Fork", "Fork")
        val knife = Category("Knife", "Knife")
        val spoon = Category("Spoon", "Spoon")
        val name = "Utensils"
        val dataset = Dataset(name, name, setOf(fork, knife, spoon))

        assert(dataset.name == name)
        assert(dataset.id == name)
        assert(dataset.categories.containsAll(setOf(fork, knife, spoon)))
    }
}