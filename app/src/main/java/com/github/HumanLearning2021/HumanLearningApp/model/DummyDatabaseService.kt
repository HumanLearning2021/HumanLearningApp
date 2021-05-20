package com.github.HumanLearning2021.HumanLearningApp.model

import android.net.Uri
import android.util.Log
import com.github.HumanLearning2021.HumanLearningApp.R
import com.google.firebase.auth.FirebaseUser
import java.util.*

/**
 * A class representing a dummy data set Interface
 * Categories are uniquely defined by their name
 */
class DummyDatabaseService internal constructor() : DatabaseService {
    private val fork = DummyCategory("Fork", "Fork")

    // fork2 allows us to have a dataset with 4 categories without needing a new test picture
    private val fork2 = DummyCategory("Fork2", "Fork2")
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

    private val forkRepPic = DummyCategorizedPicture(
        "forkPic1Id",
        fork,
        Uri.parse("android.resource://com.github.HumanLearning2021.HumanLearningApp/" + R.drawable.fork_rep)
    )
    private val knifeRepPic = DummyCategorizedPicture(
        "knifePic1Id",
        knife,
        Uri.parse("android.resource://com.github.HumanLearning2021.HumanLearningApp/" + R.drawable.knife_rep)
    )
    private val spoonRepPic = DummyCategorizedPicture(
        "spoonPic1Id",
        spoon,
        Uri.parse("android.resource://com.github.HumanLearning2021.HumanLearningApp/" + R.drawable.spoon_rep)
    )

    private val pictures: MutableSet<DummyCategorizedPicture> =
        mutableSetOf(forkPic, knifePic, spoonPic)
    private val categories: MutableSet<DummyCategory> = mutableSetOf(fork, fork2, knife, spoon)
    private val datasets: MutableSet<DummyDataset> =
        mutableSetOf(
            DummyDataset("kitchen utensils", "kitchen utensils", setOf(fork, knife, spoon)),
            DummyDataset("one category", "one category", setOf(fork)),
            DummyDataset("two categories", "two categories", setOf(fork, knife)),
            DummyDataset(
                "four categories", "four categories",
                setOf(fork, knife, spoon, fork2)
            ),
        )
    private val representativePictures: MutableMap<String, CategorizedPicture> = mutableMapOf()
    private val users = mutableMapOf<User.Id, User>()
    private val statistics = mutableMapOf<Statistic.Id, Statistic>()

    init {
        representativePictures["Fork"] = forkRepPic
        representativePictures["Fork2"] = forkRepPic
        representativePictures["Knife"] = knifeRepPic
        representativePictures["Spoon"] = spoonRepPic
    }


    override suspend fun getPicture(category: Category): CategorizedPicture? {
        require(category is DummyCategory)
        requireCategoryPresent(category)

        for (p in pictures)
            if (p.category == category) return p
        return null
    }

    override suspend fun getPicture(pictureId: Id): CategorizedPicture? {
        for (p in pictures)
            if (p.id == pictureId) return p
        return null
    }

    override suspend fun getPictureIds(category: Category): List<Id> {
        require(category is DummyCategory)
        requireCategoryPresent(category)

        val res = mutableListOf<String>()
        for (p in pictures)
            if (p.category == category) res.add(p.id)
        return res
    }

    override suspend fun getRepresentativePicture(categoryId: Id): CategorizedPicture? {
        return representativePictures[categoryId]
    }

    override suspend fun putPicture(picture: Uri, category: Category): CategorizedPicture {
        require(category is DummyCategory)
        requireCategoryPresent(category)

        val addedPicture = DummyCategorizedPicture("${UUID.randomUUID()}", category, picture)
        pictures.add(addedPicture)

        return addedPicture
    }

    private fun requireCategoryPresent(category: Category) {
        if (!categories.contains(category)) throw DatabaseService.NotFoundException(category.id)
    }

    override suspend fun getCategory(id: Id): Category? = categories.find { it.id == id }

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
        requireCategoryPresent(category)
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
        requireCategoryPresent(category)

        categories.remove(category)
        val datasetsToUpdate = datasets.filter { it.categories.contains(category) }
        val updatedDatasets =
            datasetsToUpdate.map { removeCategoryFromDataset(it, category) }
        datasets.removeAll(datasetsToUpdate)
        datasets.addAll(updatedDatasets)
    }

    override suspend fun removePicture(picture: CategorizedPicture) {
        require(picture is DummyCategorizedPicture)
        for (p in pictures) {
            if (p.id == picture.id) {
                pictures.remove(p)
                return
            }
        }
        throw DatabaseService.NotFoundException(picture.id)
    }

    override suspend fun putDataset(name: String, categories: Set<Category>): Dataset {
        val dataset = DummyDataset(name, name, categories)
        datasets.add(dataset)
        return dataset
    }

    override suspend fun getDataset(id: Id): Dataset? = datasets.find { it.id == id }


    override suspend fun deleteDataset(id: Id) {
        for (d in datasets) {
            if (d.name == id) {
                datasets.remove(d)
                return
            }
        }
        throw DatabaseService.NotFoundException(id)
    }

    override suspend fun putRepresentativePicture(picture: Uri, category: Category) {
        requireCategoryPresent(category)

        representativePictures[category.id] =
            DummyCategorizedPicture("${UUID.randomUUID()}", category, picture)
    }

    override suspend fun putRepresentativePicture(picture: CategorizedPicture) {
        require(picture is DummyCategorizedPicture)
        putRepresentativePicture(picture.picture, picture.category)
        pictures.remove(picture)
    }

    override suspend fun getDatasets(): Set<Dataset> {
        return datasets
    }

    override suspend fun removeCategoryFromDataset(
        dataset: Dataset,
        category: Category
    ): DummyDataset {
        require(dataset is DummyDataset)
        require(category is DummyCategory)
        requireDatasetPresent(dataset)

        for (c in dataset.categories) {
            if (c == category) {
                val newCategories: MutableSet<Category> = mutableSetOf()
                newCategories.apply {
                    addAll(dataset.categories)
                    remove(c)
                }
                val newDs =
                    DummyDataset(dataset.id, dataset.name, newCategories as Set<Category>)
                datasets.apply {
                    add(newDs)
                    remove(dataset)
                }
                return newDs
            }
        }
        throw DatabaseService.NotFoundException(category.id)
    }

    override suspend fun editDatasetName(dataset: Dataset, newName: String): Dataset {
        require(dataset is DummyDataset)
        requireDatasetPresent(dataset)
        val newDs = DummyDataset(dataset.id, newName, dataset.categories)
        datasets.apply {
            add(newDs)
            remove(dataset)
        }
        return newDs
    }

    private fun requireDatasetPresent(dataset: Dataset) {

        // calling datasets.contains(dataset) returned false for obviously equal datasets
        if (!datasets.contains(dataset)) {
            Log.e(
                this::class.java.simpleName,
                "The underlying database:\n $datasets\n\n does not " +
                        "contain the dataset \n $dataset"
            )
            throw DatabaseService.NotFoundException(dataset.id)
        }
    }

    override suspend fun addCategoryToDataset(dataset: Dataset, category: Category): Dataset {
        require(dataset is DummyDataset)
        require(category is DummyCategory)
        requireCategoryPresent(category)
        requireDatasetPresent(dataset)

        return if (dataset.categories.contains(category)) {
            dataset
        } else {
            val newCats = mutableSetOf<Category>()
            newCats.apply {
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
        ).also { users[it.id] = it }
    }

    override suspend fun setAdminAccess(firebaseUser: FirebaseUser, adminAccess: Boolean): User {
        val type = User.Type.FIREBASE
        val uid = firebaseUser.uid
        return DummyUser(
            type = type,
            uid = uid,
            email = firebaseUser.email,
            displayName = firebaseUser.displayName,
            isAdmin = adminAccess
        ).also { users[it.id] = it }
    }

    override suspend fun checkIsAdmin(user: User): Boolean {
        return user.isAdmin
    }

    override suspend fun getUser(type: User.Type, uid: String) = users[User.Id(uid, type)]

    override suspend fun getStatistic(userId: User.Id, datasetId: Id): Statistic? =
        statistics[Statistic.Id(userId, datasetId)]

    override suspend fun putStatistic(statistic: Statistic) {
        statistics[statistic.id] = statistic
    }
}