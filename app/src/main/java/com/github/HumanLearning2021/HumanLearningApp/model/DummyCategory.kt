package com.github.HumanLearning2021.HumanLearningApp.model

import java.io.Serializable


/**
 * Class representing a dummy implementation of the category interface
 *
 * @param name the name of the category (case-insensitive)
 */

class DummyCategory(name: String):Category, Serializable {
    override val name = name.toLowerCase()

    override fun equals(other: Any?): Boolean {
        return other is Category && other.name == name
    }

    override fun hashCode(): Int {
        return 17 + 31 * name.hashCode()
    }

}