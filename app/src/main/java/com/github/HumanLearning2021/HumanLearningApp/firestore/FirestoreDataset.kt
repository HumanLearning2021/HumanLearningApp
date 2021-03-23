package com.github.HumanLearning2021.HumanLearningApp.firestore

import com.github.HumanLearning2021.HumanLearningApp.model.Category
import com.github.HumanLearning2021.HumanLearningApp.model.Dataset

class FirestoreDataset(override val name: String, override val categories: Set<Category>,
                       override val path: String
) : Dataset, FirestoreDocument