package com.github.HumanLearning2021.HumanLearningApp.firestore

import com.github.HumanLearning2021.HumanLearningApp.model.CategorizedPicture
import com.github.HumanLearning2021.HumanLearningApp.model.Category

data class FirestoreCategory internal constructor(
    override val path: String,
    override val name: String, override val representativePicture: CategorizedPicture,
) : Category, FirestoreDocument