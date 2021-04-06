package com.github.HumanLearning2021.HumanLearningApp.firestore

import com.github.HumanLearning2021.HumanLearningApp.model.Category
import kotlinx.parcelize.Parcelize

@Parcelize
data class FirestoreCategory internal constructor(
    override val path: String, override val id: String,
    override val name: String, override val representativePicture: String?,
) : Category, FirestoreDocument