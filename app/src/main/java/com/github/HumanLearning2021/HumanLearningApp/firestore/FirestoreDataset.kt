package com.github.HumanLearning2021.HumanLearningApp.firestore

import com.github.HumanLearning2021.HumanLearningApp.model.Category
import com.github.HumanLearning2021.HumanLearningApp.model.Dataset
import kotlinx.parcelize.Parcelize

@Parcelize
class FirestoreDataset(override val id: String, override val name: String, override val categories: MutableSet<Category>,
                       override val path: String
) : Dataset, FirestoreDocument {
    override suspend fun removeCategory(category: Category): FirestoreDataset {
        TODO("Not yet implemented")
    }

    override suspend fun editDatasetName(newName: String): Dataset {
        TODO("Not yet implemented")
    }
}
