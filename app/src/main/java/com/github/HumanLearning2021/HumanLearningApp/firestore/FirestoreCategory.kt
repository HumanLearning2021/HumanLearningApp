package com.github.HumanLearning2021.HumanLearningApp.firestore

import com.github.HumanLearning2021.HumanLearningApp.model.Category
import com.github.HumanLearning2021.HumanLearningApp.model.Id
import kotlinx.parcelize.Parcelize

@Parcelize
data class FirestoreCategory internal constructor(
    override val path: String, override val id: Id,
    override val name: String
) : Category, FirestoreDocument