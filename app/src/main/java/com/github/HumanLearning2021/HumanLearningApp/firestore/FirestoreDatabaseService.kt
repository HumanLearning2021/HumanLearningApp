package com.github.HumanLearning2021.HumanLearningApp.firestore

import android.net.Uri
import android.util.Log
import com.github.HumanLearning2021.HumanLearningApp.model.*
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*

class FirestoreDatabaseService internal constructor(
    /**
     * name of a database within the Firebase App
     */
    dbName: String,
    firestore: FirebaseFirestore
) : DatabaseService {
    private val db = firestore
    private val categories = db.collection("/databases/$dbName/categories")
    private val pictures = db.collection("/databases/$dbName/pictures")
    private val datasets = db.collection("/databases/$dbName/datasets")
    private val representativePictures = db.collection("/databases/$dbName/representativePictures")
    private val users = db.collection("/databases/$dbName/users")
    private val statistics = db.collection("/databases/$dbName/statistics")
    private val storage = Firebase.storage
    private val imagesDir = storage.reference.child("$dbName/images")

    companion object {
        suspend fun getDatabaseNames(app: FirebaseApp? = null): List<String> {
            val res = Firebase.firestore(app ?: Firebase.app).collection("databases").get().await()
            return res.documents.map { doc -> doc.id }
        }
    }

    private class CategorySchema() {
        @DocumentId
        lateinit var self: DocumentReference
        lateinit var name: String

        constructor(name: String) : this() {
            this.name = name
        }

        fun toPublic() = FirestoreCategory(self.id, name)
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
            return FirestoreCategorizedPicture(self.id, cat.toPublic(), Uri.parse(url))
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
            val cats: Set<FirestoreCategory> = buildSet(categories.size) {
                for (cat in categories) {
                    val catRef = cat.get()
                    requireNotNull(catRef, { "at least one of the categories was not found" })
                    add(catRef.await().toObject(CategorySchema::class.java)!!.toPublic())
                }
            }
            return FirestoreDataset(self.id, name, cats)
        }
    }

    private class StatisticsSchema() {
        @DocumentId
        lateinit var self: DocumentReference
        var mistakeOccurrences: Int = 0
        var successOccurrences: Int = 0

        constructor(occurrences: Map<Event, Int>) : this() {
            mistakeOccurrences = occurrences[Event.MISTAKE] ?: 0
            successOccurrences = occurrences[Event.SUCCESS] ?: 0
        }

        fun toPublic() = self.id.split('+').let { (userId, datasetId) ->
            Statistic(
                Statistic.Id(
                    User.Id.fromString(userId),
                    datasetId
                ),
                mapOf(
                    Event.MISTAKE to mistakeOccurrences,
                    Event.SUCCESS to successOccurrences
                ),
            )
        }
    }

    private class UserSchema {
        @DocumentId
        lateinit var self: DocumentReference
        var displayName: String? = null
        var email: String? = null
        var isAdmin: Boolean = false
        fun toPublic() = FirestoreUser(
            displayName = displayName,
            email = email,
            isAdmin = isAdmin,
            uid = self.id.takeWhile { it != '@' },
            type = User.Type.valueOf(self.id.takeLastWhile { it != '@' }),
        )
    }

    override suspend fun getCategories(): Set<FirestoreCategory> = withContext(Dispatchers.IO) {
        val query = categories
        val cats = query.get().await().toObjects(CategorySchema::class.java)
        cats.map { cat -> cat.toPublic() }.toSet()
    }

    override suspend fun getAllPictures(category: Category): Set<FirestoreCategorizedPicture> =
        withContext(Dispatchers.IO) {
            require(category is FirestoreCategory)
            if (!categories.document(category.id).get().await().exists()) {
                throw DatabaseService.NotFoundException(category.id)
            }
            val query = pictures.whereEqualTo("category", categories.document(category.id))
            val pics = query.get().await().toObjects(PictureSchema::class.java)
            pics.map { pic -> pic.toPublic() }.toSet()
        }

    override suspend fun removeCategory(category: Category): Unit = withContext(Dispatchers.IO) {
        require(category is FirestoreCategory)
        val ref = categories.document(category.id)
        if (!ref.get().await().exists()) {
            throw DatabaseService.NotFoundException(category.id)
        }
        try {
            ref.delete().await()
        } catch (e: FirebaseFirestoreException) {
            Log.w(this.toString(), "Removing category ${category.id} failed", e)
        }
    }

    override suspend fun removePicture(picture: CategorizedPicture): Unit =
        withContext(Dispatchers.IO) {
            require(picture is FirestoreCategorizedPicture)
            val ref = pictures.document(picture.id)
            if (!ref.get().await().exists()) {
                throw DatabaseService.NotFoundException(picture.id)
            }
            try {
                ref.delete().await()
            } catch (e: FirebaseFirestoreException) {
                Log.w(this.toString(), "Removing picture ${picture.id} failed", e)
            }
            try {
                storage.getReferenceFromUrl(picture.picture.toString()).delete().await()
            } catch (e: FirebaseException) {
                Log.w(
                    this.toString(),
                    "Removing the image ${picture.picture} from storage failed",
                    e
                )
            }
        }


    override suspend fun putDataset(name: String, cats: Set<Category>): FirestoreDataset =
        withContext(Dispatchers.IO) {
            val catRefs: MutableSet<DocumentReference> = mutableSetOf()
            for (cat in cats) {
                require(cat is FirestoreCategory)
                catRefs.add(categories.document(cat.id))
            }
            val data = DatasetSchema(name, catRefs.toList())
            val documentRef = datasets.add(data).await()
            documentRef.get().await().toObject(DatasetSchema::class.java)!!.toPublic()
        }

    override suspend fun getDataset(id: Id): FirestoreDataset? = withContext(Dispatchers.IO) {
        val ds = datasets.document(id).get().await().toObject(DatasetSchema::class.java)
        ds?.toPublic()
    }

    override suspend fun deleteDataset(id: Id): Unit = withContext(Dispatchers.IO) {
        if (!datasets.document(id).get().await().exists()) {
            throw DatabaseService.NotFoundException(id)
        }
        try {
            datasets.document(id).delete().await()
        } catch (e: FirebaseFirestoreException) {
            Log.w(this.toString(), "Deleting dataset ${datasets.id} failed", e)
        }
    }

    override suspend fun putRepresentativePicture(picture: Uri, category: Category) {
        val categoryRef = categories.document(category.id)
        if (!categoryRef.get().await().exists()) {
            throw DatabaseService.NotFoundException(category.id)
        }
        val id = "${UUID.randomUUID()}"
        val imageRef = imagesDir.child(id)
        imageRef.putFile(picture).await()
        val url = "gs://${imageRef.bucket}/${imageRef.path}"
        putRepresentativePicture(url, category)
    }

    override suspend fun putRepresentativePicture(picture: CategorizedPicture) {
        val ref = pictures.document(picture.id)
        if (!ref.get().await().exists()) {
            throw DatabaseService.NotFoundException(picture.id)
        }
        try {
            ref.delete().await()
        } catch (e: FirebaseFirestoreException) {
            Log.w(this.toString(), "Removing picture ${picture.id} from ${this.db} failed", e)
        }
        putRepresentativePicture(picture.picture.toString(), picture.category)
    }

    private suspend fun putRepresentativePicture(url: String, category: Category) {
        val categoryRef = categories.document(category.id)
        if (!categoryRef.get().await().exists()) {
            throw DatabaseService.NotFoundException(category.id)
        }
        val data = PictureSchema(categoryRef, url)
        try {
            representativePictures.add(data).await()
        } catch (e: FirebaseFirestoreException) {
            Log.w(
                this.toString(),
                "Setting representative picture of category ${category.id} to picture at $url failed",
                e
            )
        }
    }

    override suspend fun getDatasets(): Set<FirestoreDataset> = withContext(Dispatchers.IO) {
        val ds = datasets.get().await().documents
        ds.mapNotNull { d -> d.toObject(DatasetSchema::class.java)?.toPublic() }.toSet()
    }

    override suspend fun removeCategoryFromDataset(
        dataset: Dataset,
        category: Category
    ): FirestoreDataset = withContext(Dispatchers.IO) {
        require(dataset is FirestoreDataset)
        require(category is FirestoreCategory)
        if (!categories.document(category.id).get().await().exists())
            throw DatabaseService.NotFoundException(category.id)
        try {
            datasets.document(dataset.id)
                .update("categories", FieldValue.arrayRemove(categories.document(category.id)))
                .await()
        } catch (e: FirebaseFirestoreException) {
            Log.w(
                this.toString(),
                "Removing category ${category.id} from dataset ${dataset.id} failed",
                e
            )
        }
        datasets.document(dataset.id).get().await().toObject(DatasetSchema::class.java)
            ?.toPublic() ?: throw DatabaseService.NotFoundException(dataset.id)
    }

    override suspend fun editDatasetName(dataset: Dataset, newName: String): FirestoreDataset =
        withContext(Dispatchers.IO) {
            require(dataset is FirestoreDataset)
            if (!datasets.document(dataset.id).get().await().exists())
                throw DatabaseService.NotFoundException(dataset.id)
            try {
                datasets.document(dataset.id).update("name", newName).await()
            } catch (e: FirebaseFirestoreException) {
                Log.w(
                    this.toString(),
                    "Renaming ${dataset.id} failed",
                    e
                )
            }
            datasets.document(dataset.id).get().await().toObject(DatasetSchema::class.java)!!
                .toPublic()
        }

    override suspend fun addCategoryToDataset(
        dataset: Dataset,
        category: Category
    ): FirestoreDataset = withContext(Dispatchers.IO) {
        require(dataset is FirestoreDataset)
        require(category is FirestoreCategory)
        if (!datasets.document(dataset.id).get().await().exists())
            throw DatabaseService.NotFoundException(dataset.id)
        try {
            datasets.document(dataset.id)
                .update("categories", FieldValue.arrayUnion(categories.document(category.id)))
                .await()
        } catch (e: FirebaseFirestoreException) {
            Log.w(
                this.toString(),
                "Adding category ${category.id} to dataset ${dataset.id} failed",
                e
            )
        }
        datasets.document(dataset.id).get().await().toObject(DatasetSchema::class.java)!!
            .toPublic()
    }

    override suspend fun updateUser(firebaseUser: FirebaseUser): FirestoreUser =
        withContext(Dispatchers.IO) {
            val uid = firebaseUser.uid
            val type = User.Type.FIREBASE
            val documentRef = users.document("$uid@$type")
            val data = UserSchema().apply {
                email = firebaseUser.email
                displayName = firebaseUser.displayName
            }
            documentRef.set(data).await()
            documentRef.get().await().toObject(UserSchema::class.java)!!.toPublic()
        }

    override suspend fun setAdminAccess(
        firebaseUser: FirebaseUser,
        adminAccess: Boolean
    ): User {
        val uid = firebaseUser.uid
        val type = User.Type.FIREBASE
        val documentRef = users.document("$uid@$type")
        val data = UserSchema().apply {
            isAdmin = adminAccess
        }
        documentRef.set(data).await()
        return documentRef.get().await().toObject(UserSchema::class.java)!!.toPublic()
    }

    override suspend fun checkIsAdmin(fireStoreUser: User): Boolean {
        return fireStoreUser.isAdmin
    }

    override suspend fun getUser(type: User.Type, uid: String): FirestoreUser? {
        val documentRef = users.document("$uid@$type")
        val user = documentRef.get().await().toObject(UserSchema::class.java)
        return user?.toPublic()
    }

    override suspend fun getStatistic(userId: User.Id, datasetId: Id): Statistic? =
        withContext(Dispatchers.IO) {
            statistics.document("$userId+$datasetId").get().await()
                .toObject(StatisticsSchema::class.java)?.toPublic()
        }

    override suspend fun putStatistic(statistic: Statistic): Unit =
        withContext(Dispatchers.IO) {
            statistic.run {
                statistics.document("$id")
                    .set(StatisticsSchema(occurrences))
            }.await()
        }

    override suspend fun getPicture(category: Category): FirestoreCategorizedPicture? =
        withContext(Dispatchers.IO) {
            require(category is FirestoreCategory)
            if (!categories.document(category.id).get().await().exists())
                throw DatabaseService.NotFoundException(category.id)
            val query =
                pictures.whereEqualTo("category", categories.document(category.id)).limit(1)
            val pic = query.get().await().toObjects(PictureSchema::class.java).getOrNull(0)
            pic?.toPublic()
        }

    override suspend fun getPicture(pictureId: Id): FirestoreCategorizedPicture? =
        withContext(Dispatchers.IO) {
            val pic =
                pictures.document(pictureId).get().await().toObject(PictureSchema::class.java)
            pic?.toPublic()
        }

    override suspend fun getPictureIds(category: Category): List<String> =
        withContext(Dispatchers.IO) {
            require(category is FirestoreCategory)
            val query = pictures.whereEqualTo("category", categories.document(category.id))
            query.get().await().map { r -> r.id }
        }

    override suspend fun getRepresentativePicture(categoryId: Id): FirestoreCategorizedPicture? =
        withContext(Dispatchers.IO) {
            val query =
                representativePictures.whereEqualTo("category", categories.document(categoryId))
                    .limit(1)
            val pic = query.get().await().toObjects(PictureSchema::class.java).getOrNull(0)
            pic?.toPublic()
        }

    override suspend fun putPicture(
        picture: Uri,
        category: Category
    ): FirestoreCategorizedPicture =
        withContext(Dispatchers.IO) {
            require(category is FirestoreCategory)
            val id = "${UUID.randomUUID()}"
            val ref = imagesDir.child(id)
            if (!categories.document(category.id).get().await().exists())
                throw DatabaseService.NotFoundException(category.id)
            ref.putFile(picture).await()
            val data =
                PictureSchema(
                    categories.document(category.id),
                    "gs://${ref.bucket}/${ref.path}"
                )
            val documentRef = pictures.add(data).await()
            documentRef.get().await().toObject(PictureSchema::class.java)!!.toPublic()
        }

    override suspend fun getCategory(id: Id): FirestoreCategory? = withContext(Dispatchers.IO) {
        val cat = categories.document(id).get().await()
            .toObject(CategorySchema::class.java)
        cat?.toPublic()
    }

    override suspend fun putCategory(categoryName: String): FirestoreCategory =
        withContext(Dispatchers.IO) {
            val data = CategorySchema(categoryName)
            val documentRef = categories.add(data).await()
            documentRef.get().await().toObject(CategorySchema::class.java)!!.toPublic()
        }
}
