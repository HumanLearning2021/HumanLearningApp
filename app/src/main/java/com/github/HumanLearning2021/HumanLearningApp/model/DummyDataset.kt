package com.github.HumanLearning2021.HumanLearningApp.model

import java.lang.IllegalArgumentException
import java.util.*

data class DummyDataset(override val name: String, override val categories: Set<Category>) : Dataset {

    override suspend fun removeCategory(category: Category): DummyDataset {
        for (c in categories) {
            if (c == category) {
                val newCategories: MutableSet<Category> = mutableSetOf()
                newCategories.apply{
                    addAll(categories)
                    remove(c)
                }
                return DummyDataset(name, newCategories as Set<Category>)
            }
        }
        throw IllegalArgumentException("The category name ${category.name} is not present in the dataset")
    }
}