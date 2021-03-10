package com.github.HumanLearning2021.HumanLearningApp.Model

class Category(val name: String, val parent:Category? = null) {

    //TODO can't put parent into equation because of null-safety
    override fun equals(other: Any?): Boolean {
        return other is Category && other.name == name
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

}