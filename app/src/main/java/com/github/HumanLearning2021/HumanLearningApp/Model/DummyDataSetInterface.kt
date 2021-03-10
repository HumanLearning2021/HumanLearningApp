package com.github.HumanLearning2021.HumanLearningApp.Model

import java.lang.IllegalArgumentException

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


}