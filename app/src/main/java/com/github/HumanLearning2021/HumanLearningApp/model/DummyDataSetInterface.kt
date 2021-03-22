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

    val pictures: MutableSet<CategorizedPicture> = mutableSetOf(forkPic, knifePic, spoonPic)




    override suspend fun getPicture(category: Category): CategorizedPicture?{
        if (!categories.contains(category)) throw IllegalArgumentException("The provided category" +
                "is not present in the dataset")

        for(p in pictures)
            if(p == DummyCategorizedPicture(category)) return p

        return null
    }

    override suspend fun putPicture(picture: android.net.Uri, category: Category): CategorizedPicture {
        if(!categories.contains(category)) throw IllegalArgumentException("The provided category" +
                "is not present in the dataset")

        val addedPicture = DummyCategorizedPicture(category)
        pictures.add(addedPicture)

        return addedPicture
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

    override suspend fun getRepresentativePicture(category: Category): CategorizedPicture {
        TODO("Not yet implemented")
    }

    override suspend fun getAllPictures(category: Category): Set<CategorizedPicture> {
        TODO("Not yet implemented")
    }

    override suspend fun removeCategory(category: Category) {
        TODO("Not yet implemented")
    }

    override suspend fun removePicture(picture: CategorizedPicture) {
        TODO("Not yet implemented")
    }
}
