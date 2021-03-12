package com.github.HumanLearning2021.HumanLearningApp.Model


/**
 * Class representing a category, which can be used to classify images
 *
 * @param name the name of the category
 */

//TODO: decide on whether case sensitivity matters
class Category(name: String) {
    val name = name.toLowerCase()


    //TODO: Odersky canEqual
    override fun equals(other: Any?): Boolean {
        return other is Category && other.name == name
    }

    override fun hashCode(): Int {
        return 17 + 31 * name.hashCode()
    }

}