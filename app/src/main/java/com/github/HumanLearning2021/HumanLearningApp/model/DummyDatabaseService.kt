package com.github.HumanLearning2021.HumanLearningApp.model

import android.net.Uri
import com.github.HumanLearning2021.HumanLearningApp.R
import java.lang.IllegalArgumentException
import java.util.*

/**
 * A class representing a dummy data set Interface
 * Categories are uniquely defined by their name
 */
class DummyDatabaseService : DatabaseService {
    private val fork = DummyCategory("Fork", "Fork", null)
    private val knife = DummyCategory("Knife", "Knife", null)
    private val spoon = DummyCategory("Spoon", "Spoon", null)

    private val forkPic = DummyCategorizedPicture(fork, Uri.parse("android.resource://com.github.HumanLearning2021.HumanLearningApp/"+R.drawable.fork))
    private val knifePic = DummyCategorizedPicture(knife, Uri.parse("android.resource://com.github.HumanLearning2021.HumanLearningApp/"+R.drawable.knife))
    private val spoonPic = DummyCategorizedPicture(spoon, Uri.parse("android.resource://com.github.HumanLearning2021.HumanLearningApp/"+R.drawable.spoon))

    private val forkRepPic = DummyCategorizedPicture(fork, Uri.parse("android.resource://com.github.HumanLearning2021.HumanLearningApp/"+R.drawable.fork_rep))
    private val knifeRepPic = DummyCategorizedPicture(knife, Uri.parse("android.resource://com.github.HumanLearning2021.HumanLearningApp/"+R.drawable.knife_rep))
    private val spoonRepPic = DummyCategorizedPicture(spoon, Uri.parse("android.resource://com.github.HumanLearning2021.HumanLearningApp/"+R.drawable.spoon_rep))

    private val pictures: MutableSet<CategorizedPicture> = mutableSetOf(forkPic, knifePic, spoonPic, forkRepPic, knifeRepPic, spoonRepPic)
    private val categories: MutableSet<Category> = mutableSetOf(fork, knife, spoon)
    private val datasets: MutableSet<Dataset> = mutableSetOf(DummyDataset("kitchen utensils", "kitchen utensils", categories))

    init {
        fork.representativePicture = forkRepPic
        knife.representativePicture = knifeRepPic
        spoon.representativePicture = spoonRepPic
    }

    override suspend fun getPicture(category: Category): CategorizedPicture?{
        if (!categories.contains(category)) throw IllegalArgumentException("The provided category" +
                " is not present in the dataset")

        for (p in pictures)
            if (p.category == category) return p
        return null
    }

    override suspend fun putPicture(picture: Uri, category: Category): CategorizedPicture {
        if(!categories.contains(category)) throw IllegalArgumentException("The provided category" +
                "is not present in the dataset")

        val addedPicture = DummyCategorizedPicture(category, picture)
        pictures.add(addedPicture)

        return addedPicture
    }

    override suspend fun getCategory(categoryId: Any): Category? {
        for (c in categories)
            if (c.id == categoryId as String) return c
        return null
    }

    override suspend fun putCategory(categoryName: String): Category {
        val category = DummyCategory(categoryName, categoryName, null)
        categories.add(category)
        return category
    }

    override suspend fun getCategories(): Set<Category> {
        return Collections.unmodifiableSet(categories)
    }

    override suspend fun getAllPictures(category: Category): Set<CategorizedPicture> {
        if (!categories.contains(category)) {
            throw IllegalArgumentException("The category ${category.name} is not present in the database")
        }
        val res: MutableSet<CategorizedPicture> = mutableSetOf()
        for (p in pictures) {
            if (p.category == category) {
                res.add(p)
            }
        }
        return res
    }

    override suspend fun removeCategory(category: Category) {
        if (categories.contains(category)) {
            categories.remove(category)
            for (d in datasets) {
                if (d.categories.contains(category)) {
                    val newDataset = d.removeCategory(category)
                    datasets.apply {
                        remove(d)
                        add(newDataset)
                    }
                }

            }
        } else {
            throw IllegalArgumentException("The category name ${category.name} is not present in the database")
        }
    }

    override suspend fun removePicture(picture: CategorizedPicture) {
        for (p in pictures) {
            if (p == picture) {
                pictures.remove(p)
                return
            }
        }
        throw IllegalArgumentException("The specified picture is not present in the database")
    }

    override suspend fun putDataset(name: String, categories: Set<Category>): Dataset {
        val dataset = DummyDataset(name, name, categories)
        datasets.add(dataset)
        return dataset
    }

    override suspend fun getDataset(id: Any): Dataset? {
        for(d in datasets)
            if(d.name == id as String) return d
        return null
    }

    override suspend fun deleteDataset(id: Any) {
        for (d in datasets) {
            if (d.name == id as String) {
                datasets.remove(d)
                return
            }
        }
        throw IllegalArgumentException("The dataset with id $id is not present in the database")
    }

    override suspend fun putRepresentativePicture(picture: Uri, category: Category) {
        for (c in categories) {
            if (c == category) {
                val newCategory = DummyCategory(c.name, c.name, DummyCategorizedPicture(category, picture))
                categories.remove(c)
                categories.add(newCategory)
                return
            }
        }
        throw IllegalArgumentException("The category name ${category.name} is not present in the database")
    }


    override fun getDatasets(): Set<Dataset> {
        return datasets
    }
}
