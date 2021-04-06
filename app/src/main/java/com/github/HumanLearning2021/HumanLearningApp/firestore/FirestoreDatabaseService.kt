package com.github.HumanLearning2021.HumanLearningApp.firestore

import android.net.Uri
import android.util.Log
import com.github.HumanLearning2021.HumanLearningApp.model.CategorizedPicture
import com.github.HumanLearning2021.HumanLearningApp.model.Category
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseService
import com.github.HumanLearning2021.HumanLearningApp.model.Dataset
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import java.util.*
import com.github.HumanLearning2021.HumanLearningApp.firestore.FirestoreCategory as FirestoreCategory1

class FirestoreDatabaseService(
    /**
     * name of a database within the Firebase App
     */
    dbName: String,
    app: FirebaseApp? = null,
) : DatabaseService {
    private val app = app ?: Firebase.app
    private val db = Firebase.firestore(this.app)
    private val categories = db.collection("/databases/$dbName/categories")
    private val pictures = db.collection("/databases/$dbName/pictures")
    private val datasets = db.collection("/databases/$dbName/datasets")
    private val storage = Firebase.storage(this.app)
    private val imagesDir = storage.reference.child("$dbName/images")

    private class CategorySchema() {
        @DocumentId
        lateinit var self: DocumentReference
        lateinit var name: String
        var representativePicture: String? = null

        constructor(name: String, representativePicture: String?) : this() {
            this.name = name
            this.representativePicture = representativePicture
        }

        fun toPublic() = FirestoreCategory1(self.path, self.id, name, representativePicture)
    }

    private class PictureSchema() {
        @DocumentId
        lateinit var self: DocumentReference
        lateinit var category: DocumentReference
        lateinit var url: String

        constructor(category: DocumentReference, url: String) : this() {
            this.category = category
            this.url = url
        }

        suspend fun toPublic(): FirestoreCategorizedPicture {
            val cat = category.get().await().toObject(CategorySchema::class.java)
            requireNotNull(cat, { "category not found" })
            return FirestoreCategorizedPicture(self.path, cat.toPublic(), url)
        }
    }

    private class DatasetSchema() {
        @DocumentId
        lateinit var self: DocumentReference
        lateinit var name: String
        lateinit var categories: List<DocumentReference>

        constructor(name: String, categories: List<DocumentReference>) : this() {
            this.name = name
            this.categories = categories.toList()
        }

        @OptIn(ExperimentalStdlibApi::class)
        suspend fun toPublic(): FirestoreDataset {
            val cats: Set<FirestoreCategory1> = buildSet(categories.size) {
                for (cat in categories) {
                    val catRef = cat.get()
                    requireNotNull(catRef, { "at least one of the categories was not found" })
                    add(catRef.await().toObject(CategorySchema::class.java)!!.toPublic())
                }
            }
            return FirestoreDataset(self.path, self.id, name, cats)
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    override suspend fun getCategories(): Set<FirestoreCategory1> {
        val query = categories
        val cats = query.get().await().toObjects(CategorySchema::class.java)
        return buildSet(cats.size) {
            for (cat in cats)
                add(cat.toPublic())
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    override suspend fun getAllPictures(category: Category): Set<FirestoreCategorizedPicture> {
        require(category is FirestoreCategory1)
        categories.document(category.id).get().addOnCompleteListener {
            if (!it.isSuccessful || it.result == null) {
                throw java.lang.IllegalArgumentException("Category with id ${category.id} is not present in the database")
            }
        }
        val query = pictures.whereEqualTo("category", db.document(category.path))
        val pics = query.get().await().toObjects(PictureSchema::class.java)
        return pics.map { pic -> pic.toPublic() }.toSet()
    }

    override suspend fun removeCategory(category: Category) {
        require(category is FirestoreCategory1)
        val ref = categories.document(category.id)
        ref.get().addOnCompleteListener {
            if (!it.isSuccessful || it.result == null) {
                throw java.lang.IllegalArgumentException("The database ${this.db} does not contain the category ${category.id}")
            }
        }
        ref.delete().addOnFailureListener {
            Log.w(
                this.toString(),
                "Removing category ${category.id} from ${this.db} failed"
            )
        }
    }

    override suspend fun removePicture(picture: CategorizedPicture) {
        require(picture is FirestoreCategorizedPicture)
        val ref = db.document(picture.path)
        ref.get().addOnCompleteListener {
            if (!it.isSuccessful || it.result == null) {
                throw IllegalArgumentException("The database ${this.db} does not contain the picture ${picture.url}")
            }
        }
        ref.delete().addOnFailureListener {
            Log.w(
                this.toString(),
                "Removing picture ${picture.url} from ${this.db} failed"
            )
        }
    }

    override suspend fun putDataset(name: String, categories: Set<Category>): FirestoreDataset {
        val catRefs: MutableSet<DocumentReference> = mutableSetOf()
        for (cat in categories) {
            require(cat is FirestoreCategory1)
            catRefs.add(db.document(cat.path))
        }
        val data = DatasetSchema(name, catRefs.toList())
        val documentRef = datasets.add(data).await()
        return documentRef.get().await().toObject(DatasetSchema::class.java)!!.toPublic()
    }

    override suspend fun getDataset(id: Any): FirestoreDataset? {
        val ds = datasets.document(id as String).get().await().toObject(DatasetSchema::class.java)
        return ds?.toPublic()
    }

    override suspend fun deleteDataset(id: Any) {
        datasets.document(id as String).get().addOnCompleteListener {
            if (!it.isSuccessful || it.result == null) {
                throw java.lang.IllegalArgumentException("Dataset with id $id is not contained in the databse")
            }
        }
        datasets.document(id as String).delete().addOnFailureListener {
            Log.w(
                this.toString(),
                "Deleting dataset ${datasets.id} from ${this.db} failed"
            )
        }
    }

    override suspend fun putRepresentativePicture(picture: Uri, category: Category) {
        require(category is FirestoreCategory1)
        val categoryRef = categories.document(category.id)
        categoryRef.get()
            .addOnCompleteListener {
                if (!it.isSuccessful || it.result == null) {
                    throw java.lang.IllegalArgumentException(
                        "The database ${this.db} does not contain the category with ${category.id}"
                    )
                }
            }
        val imageRef = imagesDir.child("${UUID.randomUUID()}")
        imageRef.putFile(picture).await()
        val url = "gs://${imageRef.bucket}/${imageRef.path}"
        categoryRef.update("representativePicture", url).addOnFailureListener {
            Log.w(
                this.toString(),
                "Setting representative picture of category ${category.id} to picture at $url failed"
            )
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    override suspend fun getDatasets(): Set<FirestoreDataset> {
        val ds = datasets.get().await().documents
        return buildSet {
            for (d in ds) {
                val obj = d.toObject(DatasetSchema::class.java)
                if (obj == null) {
                    Log.w(this.toString(), "Failed to load dataset ${d.id}")
                } else {
                    add(obj.toPublic())
                }
            }
        }
    }

    override suspend fun removeCategoryFromDataset(
        dataset: Dataset,
        category: Category
    ): FirestoreDataset {
        require(dataset is FirestoreDataset)
        require(category is FirestoreCategory1)

        val ds = datasets.document(dataset.id)
        ds.get().addOnCompleteListener {
            if (!it.isSuccessful || it.result == null) {
                throw java.lang.IllegalArgumentException("The database $this does not contain the dataset with id ${dataset.id}")
            }
        }

        val dsCategories = dataset.categories
        if (!dsCategories.contains(category)) {
            throw java.lang.IllegalArgumentException("The category ${category.id} is not contained in the dataset ${dataset.id}")
        }

        var res = dataset as FirestoreDataset
        val newCats: MutableSet<FirestoreCategory1> = mutableSetOf()
        newCats.apply {
            addAll(dsCategories)
            remove(category)
            toSet()
        }
        this.datasets.document(dataset.id).update("categories", newCats.toList())
            .addOnSuccessListener {
                res = FirestoreDataset(dataset.path, dataset.id, dataset.name, newCats)
            }.addOnFailureListener {
                Log.w(
                    this.toString(),
                    "Removing category ${category.id} from dataset ${dataset.id} failed"
                )
            }
        return res
    }

    override suspend fun editDatasetName(dataset: Dataset, newName: String): FirestoreDataset {
        require(dataset is FirestoreDataset)
        var res = dataset as FirestoreDataset
        this.datasets.document(dataset.id).update("name", newName)
            .addOnSuccessListener {
                res = FirestoreDataset(dataset.path, dataset.id, newName, dataset.categories)
            }
            .addOnFailureListener {
                Log.w(
                    this.toString(),
                    "Changing name of dataset ${dataset.id} to $newName failed"
                )
            }
        return res
    }

    override suspend fun getPicture(category: Category): FirestoreCategorizedPicture? {
        require(category is FirestoreCategory1)
        val query = pictures.whereEqualTo("category", db.document(category.path)).limit(1)
        val pic = query.get().await().toObjects(PictureSchema::class.java).getOrNull(0)
        return pic?.toPublic()
    }

    override suspend fun putPicture(picture: Uri, category: Category): FirestoreCategorizedPicture {
        require(category is FirestoreCategory1)
        val ref = imagesDir.child("${UUID.randomUUID()}")
        ref.putFile(picture).await()
        val data = PictureSchema(db.document(category.path), "gs://${ref.bucket}/${ref.path}")
        val documentRef = pictures.add(data).await()
        return documentRef.get().await().toObject(PictureSchema::class.java)!!.toPublic()
    }

    override suspend fun getCategory(categoryId: Any): FirestoreCategory1? {
        val cat = categories.document(categoryId as String).get().await()
            .toObject(CategorySchema::class.java)
        return cat?.toPublic()
    }

    override suspend fun putCategory(categoryName: String): FirestoreCategory1 {
        val data = CategorySchema(categoryName, null)
        val documentRef = categories.add(data).await()
        return documentRef.get().await().toObject(CategorySchema::class.java)!!.toPublic()
    }
}