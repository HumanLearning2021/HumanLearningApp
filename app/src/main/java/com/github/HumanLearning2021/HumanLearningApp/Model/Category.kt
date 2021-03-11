package com.github.HumanLearning2021.HumanLearningApp.Model

class Category(val name: String) {

    //TODO can't put parent into equation because of null-safety
    override fun equals(other: Any?): Boolean {
        return other is Category && other.name.equals(name, ignoreCase = true)
    }

    override fun hashCode(): Int {
        return 17 + 31 * name.hashCode()
    }

}