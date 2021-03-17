package com.github.HumanLearning2021.HumanLearningApp.model

import android.graphics.drawable.Drawable
import java.lang.IllegalArgumentException
import java.util.*

/**
 * a class representing a dummy data set Interface
 */
class DummyDatasetInterface : DatasetInterface {
    private val fork = DummyCategory("Fork")
    private val knife = DummyCategory("Knife")
    private val spoon = DummyCategory("Spoon")

    val categories: MutableSet<Category> = mutableSetOf(fork, knife, spoon)

    private val forkPic = DummyCategorizedPicture(fork)
    private val knifePic = DummyCategorizedPicture(knife)
    private val spoonPic = DummyCategorizedPicture(spoon)


    override suspend fun getPicture(category: Category): CategorizedPicture?{
        if (!categories.contains(category)) throw IllegalArgumentException("The provided category" +
                "is not present in the dataset")
        if(category != fork && category != spoon && category != knife) return null

        return when (category) {
            fork -> forkPic
            knife -> knifePic
            spoon -> spoonPic
            else -> null
        }
    }

    override suspend fun putPicture(picture: Drawable, category: Category): CategorizedPicture {
        if(!categories.contains(category)) throw IllegalArgumentException("The provided category" +
                "is not present in the dataset")

        return DummyCategorizedPicture(category)
    }

    override suspend fun getCategory(categoryName: String): Category? {
        for(c in categories)
            if(c.name == categoryName) return c
        return null
    }


    override suspend fun putCategory(categoryName: String): Category {
        val category = DummyCategory(categoryName)
        categories.add(category)
        return category
    }

    override suspend fun getCategories(): Set<Category> {
        return Collections.unmodifiableSet(categories)
    }
}
