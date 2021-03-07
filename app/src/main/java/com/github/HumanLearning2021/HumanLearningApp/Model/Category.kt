package com.github.HumanLearning2021.HumanLearningApp.Model


/**
 * Class representing a category, which can be used to classify images
 *
 * @param name the name of the category
 */
class Category(val name: String) {

    override fun equals(other: Any?): Boolean {
        return other is Category && other.name.equals(name, ignoreCase = true)
    }

    override fun hashCode(): Int {
        return 17 + 31 * name.hashCode()
    }

}