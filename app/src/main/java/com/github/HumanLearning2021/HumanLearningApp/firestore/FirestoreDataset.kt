package com.github.HumanLearning2021.HumanLearningApp.firestore

import com.github.HumanLearning2021.HumanLearningApp.model.Category
import com.github.HumanLearning2021.HumanLearningApp.model.Dataset

<<<<<<< HEAD
class FirestoreDataset(override val name: String, override val categories: MutableSet<Category>,
                       override val path: String
) : Dataset, FirestoreDocument {
    override suspend fun removeCategory(category: Category): Dataset {
        TODO("Not yet implemented")
    }
}
=======
class FirestoreDataset(override val name: String, override val categories: Set<Category>,
                       override val path: String
) : Dataset, FirestoreDocument
>>>>>>> main
