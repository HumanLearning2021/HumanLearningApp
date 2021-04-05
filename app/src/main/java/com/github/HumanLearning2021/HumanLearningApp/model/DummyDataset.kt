package com.github.HumanLearning2021.HumanLearningApp.model

import kotlinx.parcelize.Parcelize
import java.lang.IllegalArgumentException

@Parcelize
data class DummyDataset(override val id: String, override val name: String, override val categories: Set<Category>
) : Dataset {

    @Deprecated("This method should no longer be used because it won't be available outside of the dummy implementation",
    ReplaceWith("DummyDatabaseService.removeCategoryFromDataset", "com.github.HumanLearning2021.HumanLearningApp.model"))
    fun removeCategory(category: Category): DummyDataset {
        for (c in categories) {
            if (c == category) {
                val newCategories: MutableSet<Category> = mutableSetOf()
                newCategories.apply{
                    addAll(categories)
                    remove(c)
                }
                return DummyDataset(id, name, newCategories as Set<Category>)
            }
        }
        throw IllegalArgumentException("The category ${category.id} named ${category.name} is not present in the dataset")
    }

    @Deprecated("This method should no longer be used because it won't be available outside of the dummy implementation",
        ReplaceWith("DummyDatabaseService.editDatasetName", "com.github.HumanLearning2021.HumanLearningApp.model.removeCategory"))
    fun editDatasetName(newName: String): Dataset {
        return DummyDataset(id, newName, categories)
    }
}