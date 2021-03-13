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
     */
    override suspend fun getPicture(category: Category): CategorizedPicture =
        when (category) {
            fork -> forkPic
            knife -> knifePic
            else -> spoonPic

        }
}
