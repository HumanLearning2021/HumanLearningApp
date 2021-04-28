package com.github.HumanLearning2021.HumanLearningApp.model

import android.net.Uri
import com.github.HumanLearning2021.HumanLearningApp.R
import com.google.firebase.auth.FirebaseUser
import java.util.*

/**
 * A class representing a dummy data set Interface
 * Categories are uniquely defined by their name
 */
class DummyDatabaseService internal constructor() : DatabaseService {
    private val fork = DummyCategory("Fork", "Fork")
    private val knife = DummyCategory("Knife", "Knife")
    private val spoon = DummyCategory("Spoon", "Spoon")

    private val forkPic = DummyCategorizedPicture(
        "forkpicid",
        fork,
        Uri.parse("android.resource://com.github.HumanLearning2021.HumanLearningApp/" + R.drawable.fork)
    )
    private val knifePic = DummyCategorizedPicture(
        "knifepicid",
        knife,
        Uri.parse("android.resource://com.github.HumanLearning2021.HumanLearningApp/" + R.drawable.knife)
    )
    private val spoonPic = DummyCategorizedPicture(
        "spoonpicid",
        spoon,
        Uri.parse("android.resource://com.github.HumanLearning2021.HumanLearningApp/" + R.drawable.spoon)
    )

    private val forkRepPic = DummyCategorizedPicture("forkPic1Id", fork, Uri.parse("android.resource://com.github.HumanLearning2021.HumanLearningApp/"+R.drawable.fork_rep))
    private val knifeRepPic = DummyCategorizedPicture("knifePic1Id", knife, Uri.parse("android.resource://com.github.HumanLearning2021.HumanLearningApp/"+R.drawable.knife_rep))
    private val spoonRepPic = DummyCategorizedPicture("spoonPic1Id", spoon, Uri.parse("android.resource://com.github.HumanLearning2021.HumanLearningApp/"+R.drawable.spoon_rep))

    private val pictures: MutableSet<CategorizedPicture> = mutableSetOf(forkPic, knifePic, spoonPic)
    private val categories: MutableSet<Category> = mutableSetOf(fork, knife, spoon)
    private val datasets: MutableSet<Dataset> =
        mutableSetOf(DummyDataset("kitchen utensils", "kitchen utensils", categories))
    private val representativePictures: MutableMap<String, CategorizedPicture> = mutableMapOf()
    private val users = mutableMapOf<Pair<User.Type, String>, User>()

    init {
        representativePictures["Fork"] = forkRepPic
        representativePictures["Knife"] = knifeRepPic
        representativePictures["Spoon"] = spoonRepPic
    }


    override suspend fun getPicture(category: Category): CategorizedPicture? {
        require(category is DummyCategory)
        if (!categories.contains(category)) throw IllegalArgumentException(
            "The provided category is not present in the dataset"
        )

        for (p in pictures)
            if (p.category == category) return p
        return null
    }

    override suspend fun getPicture(pictureId: Any): CategorizedPicture? {
        require(pictureId is String)
        for (p in pictures)
            if (p.id == pictureId) return p
        return null
    }

    override suspend fun getPictureIds(category: Category): List<Any> {
        require(category is DummyCategory)
        if (!categories.contains(category)) throw IllegalArgumentException(
            "The provided category is not present in the dataset"
        )

        val res = mutableListOf<String>()
        for (p in pictures)
            if (p.category == category) res.add(p.id)
        return res
    }

    override suspend fun getRepresentativePicture(categoryId: Any): CategorizedPicture? {
        return representativePictures[categoryId]
    }

    override suspend fun putPicture(picture: Uri, category: Category): CategorizedPicture {
        require(category is DummyCategory)
        if(!categories.contains(category)) throw IllegalArgumentException("The provided category" +
                "is not present in the dataset")

        val addedPicture = DummyCategorizedPicture("${UUID.randomUUID()}", category, picture)
        pictures.add(addedPicture)

        return addedPicture
    }

    override suspend fun getCategory(categoryId: Any): Category? {
        for (c in categories)
            if (c.id == categoryId as String) return c
        return null
    }

    override suspend fun putCategory(categoryName: String): Category {
        val category = DummyCategory(categoryName, categoryName)
        categories.add(category)
        return category
    }

    override suspend fun getCategories(): Set<Category> {
        return Collections.unmodifiableSet(categories)
    }

    override suspend fun getAllPictures(category: Category): Set<CategorizedPicture> {
        require(category is DummyCategory)
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
        require(category is DummyCategory)
        if (categories.contains(category)) {
            categories.remove(category)
            for (d in datasets) {
                if (d.categories.contains(category)) {
                    val newDataset = removeCategoryFromDataset(d, category)
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
        require(picture is DummyCategorizedPicture)
        for (p in pictures) {
            if (p.id == picture.id) {
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
        for (d in datasets)
            if (d.name == id as String) return d
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
        if (!categories.contains(category)) {
            throw IllegalArgumentException("The category name ${category.name} is not present in the database")

        }
        representativePictures[category.id as String] = DummyCategorizedPicture("${UUID.randomUUID()}", category, picture)
    }


    override suspend fun getDatasets(): Set<Dataset> {
        return datasets
    }

    override suspend fun removeCategoryFromDataset(dataset: Dataset, category: Category): Dataset {
        require(dataset is DummyDataset)
        require(category is DummyCategory)
        if (!datasets.contains(dataset)) {
            throw IllegalArgumentException("The underlying database does not contain the dataset ${dataset.id}")
        }
        val dsCategories = dataset.categories
        for (c in dsCategories) {
            if (c == category) {
                val newCategories: MutableSet<Category> = mutableSetOf()
                newCategories.apply{
                    addAll(categories)
                    remove(c)
                }
                val newDs = DummyDataset(dataset.id, dataset.name, newCategories as Set<Category>)
                this.datasets.apply {
                    add(newDs)
                    remove(dataset)
                }
                return newDs
            }
        }
        throw IllegalArgumentException("The category ${category.id} named ${category.name} is not present in the dataset")
    }

    override suspend fun editDatasetName(dataset: Dataset, newName: String): Dataset {
        require(dataset is DummyDataset)
        if (!datasets.contains(dataset)) {
            throw IllegalArgumentException("The underlying database does not contain the dataset ${dataset.name}")
        }
        val newDs = DummyDataset(dataset.id, newName, dataset.categories)
        this.datasets.apply {
            add(newDs)
            remove(dataset)
        }
        return newDs
    }

    override suspend fun addCategoryToDataset(dataset: Dataset, category: Category): Dataset {
        require(dataset is DummyDataset)
        require(category is DummyCategory)
        if (!categories.contains(category)) {
            throw java.lang.IllegalArgumentException("The underlying database does not " +
                    "contain the category ${category.name}")
        }
        // calling datasets.contains(dataset) returned false for obviously equal datasets
        // TODO : discuss this
//        if (!datasets.contains(dataset)) {
        if (datasets.find{it == dataset} == null) {
            throw IllegalArgumentException("The underlying database:\n $datasets\n\n does not " +
                    "contain the dataset \n $dataset")
        }
        return if (dataset.categories.contains(category)) {
            dataset
        } else {
            val newCats = mutableSetOf<Category>()
            newCats.apply{
                addAll(dataset.categories)
                add(category)
            }
            val newDs = DummyDataset(dataset.id, dataset.name, newCats)
            datasets.apply {
                remove(dataset)
                add(newDs)
            }
            newDs
        }
    }

    override suspend fun updateUser(firebaseUser: FirebaseUser): User {
        val type = User.Type.FIREBASE
        val uid = firebaseUser.uid
        return DummyUser(
            type = type,
            uid = uid,
            email = firebaseUser.email,
            displayName = firebaseUser.displayName,
        ).also { users[Pair(type, uid)] = it }
    }

    override suspend fun getUser(type: User.Type, uid: String) = users[Pair(type, uid)]
}
