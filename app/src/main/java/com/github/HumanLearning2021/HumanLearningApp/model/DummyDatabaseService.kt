package com.github.HumanLearning2021.HumanLearningApp.model

import android.net.Uri
import java.lang.IllegalArgumentException
import java.util.*

/**
 * a class representing a dummy data set Interface
 */
class DummyDatabaseService : DatabaseService {
    private val fork = DummyCategory("Fork", null)
    private val knife = DummyCategory("Knife", null)
    private val spoon = DummyCategory("Spoon", null)

    private val categories: MutableSet<Category> = mutableSetOf(fork, knife, spoon)

    private val forkPic = DummyCategorizedPicture(fork)
    private val knifePic = DummyCategorizedPicture(knife)
    private val spoonPic = DummyCategorizedPicture(spoon)

    private val pictures: MutableSet<CategorizedPicture> = mutableSetOf(forkPic, knifePic, spoonPic)

    private val datasets: MutableSet<Dataset> = mutableSetOf(DummyDataset("kitchen utensils", categories))


    override suspend fun getPicture(category: Category): CategorizedPicture?{
        if (!categories.contains(category)) throw IllegalArgumentException("The provided category" +
                " is not present in the dataset")

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
        val category = DummyCategory(categoryName, null)
        categories.add(category)
        return category
    }

    override suspend fun getCategories(): Set<Category> {
        return Collections.unmodifiableSet(categories)
    }

    override suspend fun getRepresentativePicture(category: Category): CategorizedPicture? {
        return category.representativePicture
    }

    override suspend fun getAllPictures(category: Category): Set<CategorizedPicture> {
        val res: MutableSet<CategorizedPicture> = mutableSetOf()
        for (p in pictures) {
            if (p.category == category) {
                res.add(p)
            }
        }
        return res
    }

    override suspend fun removeCategory(datasetName: String, category: Category) {
        TODO("Not yet implemented")
    }

    override suspend fun removePicture(picture: CategorizedPicture) {
        TODO("Not yet implemented")
    }

    override suspend fun putDataset(name: String, categories: Set<Category>): Dataset {
        val dataset = DummyDataset(name, categories)
        datasets.add(dataset)
        return dataset
    }

    override suspend fun getDataset(name: String): Dataset? {
        for(d in datasets)
            if(d.name == name) return d
        return null
    }

    override suspend fun deleteDataset(name: String) {
        TODO("Not yet implemented")
    }

    override suspend fun putRepresentativePicture(picture: Uri, category: Category) {
        TODO("Not yet implemented")
    }

    override suspend fun putRepresentativePicture(picture: CategorizedPicture) {
        TODO("Not yet implemented")
    }

    override fun getDatasetNames(): Set<String> {
        TODO("Not yet implemented")
    }

    override fun editDatasetName(oldName: String, newName: String) {
        TODO("Not yet implemented")
    }
}
