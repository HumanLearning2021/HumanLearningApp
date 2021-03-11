package com.github.HumanLearning2021.HumanLearningApp.Model

import java.lang.IllegalArgumentException

/**
 * a class representing the dummy interface to a data set
 */
class DummyDataSetInterface: DataSetInterface {
    val currentDataSet = DummyDataSet()

    override fun getPicture(categoryString: String): CategorizedPicture {
        return currentDataSet.getPicture(getCategory(categoryString))

    }


    private fun getCategory(name: String):Category {
        for(category in currentDataSet.categories) {
            if(category.name.equals(name, ignoreCase = true)) return category
        }
        throw IllegalArgumentException("no category found that matches name")
    }

    override fun equals(other: Any?): Boolean {
        return other is DummyDataSetInterface && other.currentDataSet == currentDataSet
    }

    override fun hashCode(): Int {
        return 17 + 31*currentDataSet.hashCode()
    }


}