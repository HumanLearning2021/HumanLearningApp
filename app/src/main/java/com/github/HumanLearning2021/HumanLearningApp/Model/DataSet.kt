package com.github.HumanLearning2021.HumanLearningApp.Model


//TODO: what to store in dataset: just image/category and assemble later, or image/options

abstract class DataSet(val name: String, val admin: Admin, val categories: Set<Category>, val comVoorLevel: Int) {
    abstract fun getPicture(category: Category): CategorizedPicture

    //fun getCategories: Set<Category> = categories
}