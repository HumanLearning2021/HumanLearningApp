package com.github.HumanLearning2021.HumanLearningApp.Model


//TODO: what to store in dataset: just image/category and assemble later, or image/options

abstract class DataSet(
    val name: String,
    val admin: Admin,
    val categories: Set<Category>,
    val comVoorLevel: Int
) {
    abstract fun getPicture(category: Category): CategorizedPicture

    override fun equals(other: Any?): Boolean {
        return other is DataSet && other.name == name && other.admin == admin && other.comVoorLevel == comVoorLevel && other.categories == categories
    }

    override fun hashCode(): Int {
        var result = 17
        result = 31 * result + name.hashCode()
        result = 31 * result + admin.hashCode()
        result = 31 * result + categories.hashCode()
        result = 31 * result + comVoorLevel
        return result
    }

}