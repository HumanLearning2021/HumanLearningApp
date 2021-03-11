package com.github.HumanLearning2021.HumanLearningApp.Model


/**
 * A class representing a data set of images
 *
 * @param name the name of the data set
 * @param admin the admin who created the data set
 * @param categories the set of categories of images present in the dataset
 * @param comVoorLevel the ComVoor level (1-5) describing the difficulty of the data set
 */
abstract class DataSet(
    val name: String,
    val admin: Admin,
    val categories: Set<Category>,
    val comVoorLevel: Int
) {

    /**
     * A function to retrieve a picture from the data set given a category
     *
     * @param category the category of the image to be retrieved
     */
<<<<<<< HEAD
    abstract fun getPicture(category: Category): Task<CategorizedPicture>
=======
    abstract suspend fun getPicture(category: Category): CategorizedPicture
>>>>>>> 01f8cbdd24325478107733d762bf14268ed46a70

    override fun equals(other: Any?): Boolean {
        return (other is DataSet && other.name == name && other.admin == admin &&
                other.comVoorLevel == comVoorLevel && other.categories == categories)
    }

    override fun hashCode(): Int {
        var result = 17
        result = 31 * result + name.hashCode()
        result = 31 * result + admin.hashCode()
        result = 31 * result + categories.hashCode()
        result = 31 * result + comVoorLevel
        return result
    }

<<<<<<< HEAD
}
=======
}
>>>>>>> 01f8cbdd24325478107733d762bf14268ed46a70
