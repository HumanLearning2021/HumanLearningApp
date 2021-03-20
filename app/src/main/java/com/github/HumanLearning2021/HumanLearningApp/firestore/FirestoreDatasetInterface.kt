package com.github.HumanLearning2021.HumanLearningApp.firestore

import com.github.HumanLearning2021.HumanLearningApp.model.CategorizedPicture
import com.github.HumanLearning2021.HumanLearningApp.model.Category
import com.github.HumanLearning2021.HumanLearningApp.model.DatasetInterface
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app
import kotlinx.coroutines.tasks.await
import java.io.Serializable

class FirestoreDatasetInterface(
    dbName: String,
    app: FirebaseApp? = null,
) : DatasetInterface {
    private val app = app ?: Firebase.app
    private val db: FirebaseFirestore = Firebase.firestore(this.app)
    private val dbPrefix: String = "/databases/${dbName}"

    private class CategorySchema {
        @DocumentId
        lateinit var self: DocumentReference
        lateinit var name: String
        fun toPublic() = FirestoreCategory(self.path, name)
    }

    private class PictureSchema {
        @DocumentId
        lateinit var self: DocumentReference
        lateinit var category: DocumentReference
        lateinit var url: String
        suspend fun toPublic(): FirestoreCategorizedPicture {
            val cat = category.get().await().toObject(CategorySchema::class.java)
            requireNotNull(cat, { "category not found" })
            return FirestoreCategorizedPicture(self.path, cat.toPublic(), url)
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    override suspend fun getCategories(): Set<FirestoreCategory> {
        val query = db.collection("$dbPrefix/categories")
        val cats = query.get().await().toObjects(CategorySchema::class.java)
        return buildSet(cats.size) {
            for (cat in cats)
                add(cat.toPublic())
        }
    }

    override suspend fun getPicture(category: Category): FirestoreCategorizedPicture? {
        val cat: FirestoreCategory = category as FirestoreCategory
        val query =
            db.collection("$dbPrefix/pictures").whereEqualTo("category", db.document(cat.path))
                .limit(1)
        val pic = query.get().await().toObjects(PictureSchema::class.java).getOrNull(0)
        return pic?.toPublic()
    }

    override suspend fun putPicture(picture: Serializable, category: Category): CategorizedPicture {
        TODO("Not yet implemented")
    }

    override suspend fun getCategory(categoryName: String): Category? {
        TODO("Not yet implemented")
    }

    override suspend fun putCategory(categoryName: String): Category {
        TODO("Not yet implemented")
    }
}