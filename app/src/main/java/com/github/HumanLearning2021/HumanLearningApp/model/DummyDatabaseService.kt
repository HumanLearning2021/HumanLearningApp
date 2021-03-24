package com.github.HumanLearning2021.HumanLearningApp.model

import android.net.Uri
import com.github.HumanLearning2021.HumanLearningApp.R
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

    private val forkPic = DummyCategorizedPicture(fork, Uri.parse("android.resource://com.github.HumanLearning2021.HumanLearningApp/"+R.drawable.fork))
    private val knifePic = DummyCategorizedPicture(knife, Uri.parse("android.resource://com.github.HumanLearning2021.HumanLearningApp/"+R.drawable.knife))
    private val spoonPic = DummyCategorizedPicture(spoon, Uri.parse("android.resource://com.github.HumanLearning2021.HumanLearningApp/"+R.drawable.spoon))

    private val pictures: MutableSet<CategorizedPicture> = mutableSetOf(forkPic, knifePic, spoonPic)

    private val datasets: MutableSet<Dataset> = mutableSetOf(DummyDataset("kitchen utensils", categories))


    override suspend fun getPicture(category: Category): CategorizedPicture?{
        if (!categories.contains(category)) throw IllegalArgumentException("The provided category" +
                " is not present in the dataset")

        for (p in pictures)
            if(p.category == category) return p
        return null
    }

    override suspend fun putPicture(picture: android.net.Uri, category: Category): CategorizedPicture {
        if(!categories.contains(category)) throw IllegalArgumentException("The provided category" +
                "is not present in the dataset")

        val addedPicture = DummyCategorizedPicture(category, picture)
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

    override suspend fun removeCategory(category: Category) {
        val found = false
        for (c in categories) {
            if (c == category) {
                categories.remove(c)
            }
        }
        if (found) {
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
        for (d in datasets) {
            if (d.name == name) {
                datasets.remove(d)
                return
            }
        }
        throw IllegalArgumentException("The dataset named $name is not present in the database")
    }

    override suspend fun putRepresentativePicture(picture: Uri, category: Category) {
        for (c in categories) {
            if (c == category) {
                val newCategory = DummyCategory(c.name, DummyCategorizedPicture(category, picture))
                categories.remove(c)
                categories.add(newCategory)
                return
            }
        }
        throw IllegalArgumentException("The category name ${category.name} is not present in the database")
    }

    override suspend fun putRepresentativePicture(picture: CategorizedPicture) {
        putRepresentativePicture((picture as DummyCategorizedPicture).picture, picture.category)
    }

    override fun getDatasetNames(): Set<String> {
        return datasets.toSet().map {d -> d.name}.toSet()
    }

    override fun editDatasetName(oldName: String, newName: String) {
        for (d in datasets) {
            if (d.name == oldName) {
                val newDataset = DummyDataset(newName, d.categories)
                datasets.remove(d)
                datasets.add(newDataset)
                return
            }
        }
        throw IllegalArgumentException("The dataset named $oldName is not present in the database")
    }
}
