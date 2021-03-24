package com.github.HumanLearning2021.HumanLearningApp.model

import java.lang.IllegalArgumentException

data class DummyDataset(override val name: String, override val categories: MutableSet<Category>) : Dataset {

    override suspend fun removeCategory(category: Category) {
        for (c in categories) {
            if (c == category) {
                categories.remove(c)
                return
            }
        }
        throw IllegalArgumentException("The category name ${category.name} is not present in the dataset")
    }
}