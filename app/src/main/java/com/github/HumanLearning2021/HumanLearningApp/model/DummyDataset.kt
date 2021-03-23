package com.github.HumanLearning2021.HumanLearningApp.model

data class DummyDataset(override val name: String, override val categories: Set<Category>) : Dataset {
}