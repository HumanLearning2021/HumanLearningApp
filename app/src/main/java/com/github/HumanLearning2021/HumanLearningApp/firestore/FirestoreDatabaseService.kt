package com.github.HumanLearning2021.HumanLearningApp.firestore

import android.net.Uri
import android.util.Log
import com.github.HumanLearning2021.HumanLearningApp.hilt.ProductionDatabaseName
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
import javax.inject.Inject

/**
 * Class used to interact with the Firebase Firestore database
 * @param dbName name of the database
 * @param firestore Firebase Firestore the application uses
 */
class FirestoreDatabaseService @Inject internal constructor(
    @ProductionDatabaseName dbName: String,
    firestore: FirebaseFirestore
) : DatabaseService {
    private val db = firestore
    private val categories = db.collection("/databases/$dbName/categories")
    private val pictures = db.collection("/databases/$dbName/pictures")
    private val datasets = db.collection("/databases/$dbName/datasets")
    private val representativePictures = db.collection("/databases/$dbName/representativePictures")
    private val users = db.collection("/databases/$dbName/users")
    private val storage = Firebase.storage
    private val imagesDir = storage.reference.child("$dbName/images")

    companion object {
        /**
         * Function to get the names of all the available databases
         * @param app Firebase where the databases are stored
         * @return a list containing the names of all the available databases
         */
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

        fun toPublic() = Category(self.id, name)
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

        suspend fun toPublic(): CategorizedPicture {
            val cat = category.get().await().toObject(CategorySchema::class.java)
            requireNotNull(cat, { "category not found" })
            return CategorizedPicture(self.id, cat.toPublic(), Uri.parse(url))
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

        suspend fun toPublic(): Dataset {
            val cats = categories.map { c ->
                c.get().await().toObject(CategorySchema::class.java)?.toPublic()
                    ?: throw DatabaseService.NotFoundException(c.id)
            }.toSet()
            return Dataset(self.id, name, cats)
        }
    }

    private class UserSchema {
        @DocumentId
        lateinit var self: DocumentReference
        var displayName: String? = null
        var email: String? = null
        var isAdmin: Boolean = false
        fun toPublic() = User(
            displayName = displayName,
            email = email,
            isAdmin = isAdmin,
            uid = self.id.takeWhile { it != '@' },
            type = User.Type.valueOf(self.id.takeLastWhile { it != '@' }),
        )
    }

    override suspend fun getCategories(): Set<Category> = withContext(Dispatchers.IO) {
        val query = categories
        val cats = query.get().await().toObjects(CategorySchema::class.java)
        cats.map { cat -> cat.toPublic() }.toSet()
    }

    override suspend fun getAllPictures(category: Category): Set<CategorizedPicture> =
        withContext(Dispatchers.IO) {
            if (!categories.document(category.id).get().await().exists()) {
                throw DatabaseService.NotFoundException(category.id)
            }
            val query = pictures.whereEqualTo("category", categories.document(category.id))
            val pics = query.get().await().toObjects(PictureSchema::class.java)
            pics.map { pic -> pic.toPublic() }.toSet()
        }

    override suspend fun removeCategory(category: Category): Unit = withContext(Dispatchers.IO) {
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


    override suspend fun putDataset(name: String, categories: Set<Category>): Dataset =
        withContext(Dispatchers.IO) {
            val catRefs: MutableSet<DocumentReference> = mutableSetOf()
            for (cat in categories) {
                catRefs.add(this@FirestoreDatabaseService.categories.document(cat.id))
            }
            val data = DatasetSchema(name, catRefs.toList())
            val documentRef = datasets.add(data).await()
            documentRef.get().await().toObject(DatasetSchema::class.java)!!.toPublic()
        }

    override suspend fun getDataset(id: Id): Dataset? = withContext(Dispatchers.IO) {
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

    override suspend fun getDatasets(): Set<Dataset> = withContext(Dispatchers.IO) {
        val ds = datasets.get().await().documents
        ds.mapNotNull { d -> d.toObject(DatasetSchema::class.java)?.toPublic() }.toSet()
    }

    override suspend fun removeCategoryFromDataset(
        dataset: Dataset,
        category: Category
    ): Dataset = withContext(Dispatchers.IO) {
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

    override suspend fun editDatasetName(dataset: Dataset, newName: String): Dataset =
        withContext(Dispatchers.IO) {
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
    ): Dataset = withContext(Dispatchers.IO) {
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

    override suspend fun updateUser(firebaseUser: FirebaseUser): User =
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

    override suspend fun checkIsAdmin(user: User): Boolean {
        return user.isAdmin
    }

    override suspend fun getUser(type: User.Type, uid: String): User? {
        val documentRef = users.document("$uid@$type")
        val user = documentRef.get().await().toObject(UserSchema::class.java)
        return user?.toPublic()
    }

    override suspend fun getPicture(category: Category): CategorizedPicture? =
        withContext(Dispatchers.IO) {
            if (!categories.document(category.id).get().await().exists())
                throw DatabaseService.NotFoundException(category.id)
            val query =
                pictures.whereEqualTo("category", categories.document(category.id)).limit(1)
            val pic = query.get().await().toObjects(PictureSchema::class.java).getOrNull(0)
            pic?.toPublic()
        }

    override suspend fun getPicture(pictureId: Id): CategorizedPicture? =
        withContext(Dispatchers.IO) {
            val pic =
                pictures.document(pictureId).get().await().toObject(PictureSchema::class.java)
            pic?.toPublic()
        }

    override suspend fun getPictureIds(category: Category): List<String> =
        withContext(Dispatchers.IO) {
            val query = pictures.whereEqualTo("category", categories.document(category.id))
            query.get().await().map { r -> r.id }
        }

    override suspend fun getRepresentativePicture(categoryId: Id): CategorizedPicture? =
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
    ): CategorizedPicture =
        withContext(Dispatchers.IO) {
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

    override suspend fun getCategory(id: Id): Category? = withContext(Dispatchers.IO) {
        val cat = categories.document(id).get().await()
            .toObject(CategorySchema::class.java)
        cat?.toPublic()
    }

    override suspend fun putCategory(categoryName: String): Category =
        withContext(Dispatchers.IO) {
            val data = CategorySchema(categoryName)
            val documentRef = categories.add(data).await()
            documentRef.get().await().toObject(CategorySchema::class.java)!!.toPublic()
        }
}
