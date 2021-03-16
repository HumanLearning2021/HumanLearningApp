package com.github.HumanLearning2021.HumanLearningApp.Model

import java.lang.IllegalArgumentException

/**
 * a class representing a dummy data set Interface
 */
class DummyDataSetInterface : DataSetInterface {
    private val fork = DummyCategory("Fork")
    private val knife = DummyCategory("Knife")
    private val spoon = DummyCategory("Spoon")

    val categories: Set<Category> = setOf(fork, knife, spoon)

    private val forkPic = DummyCategorizedPicture(fork)
    private val knifePic = DummyCategorizedPicture(knife)
    private val spoonPic = DummyCategorizedPicture(spoon)


    /**
     * A function to retrieve a picture from the data set given a category
     *
     * @param category the category of the image to be retrieved
     * @return a CategorizedPicture from the desired category. Null if no picture of the desired
     * category is present in the dataset.
     */
    override suspend fun getPicture(category: Category): CategorizedPicture? {
        if (!categories.contains(category)) return null

        return when (category) {
            fork -> forkPic
            knife -> knifePic
            spoon -> spoonPic
            else -> null
        }
    }
}
