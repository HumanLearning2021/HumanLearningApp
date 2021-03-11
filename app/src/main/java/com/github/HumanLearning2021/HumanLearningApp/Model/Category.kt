package com.github.HumanLearning2021.HumanLearningApp.Model


/**
 * Class representing a category, which can be used to classify images
 *
 * @param name the name of the category
 */
class Category(val name: String) {


    //TODO: Odersky canEqual
    override fun equals(other: Any?): Boolean {
        return other is Category && other.name == (name.toLowerCase())
    }

    override fun hashCode(): Int {
        return 17 + 31 * name.toLowerCase().hashCode()
    }

}