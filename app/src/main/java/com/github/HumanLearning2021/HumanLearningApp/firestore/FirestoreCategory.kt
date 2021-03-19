package com.github.HumanLearning2021.HumanLearningApp.firestore

import com.github.HumanLearning2021.HumanLearningApp.model.Category

data class FirestoreCategory(
    val path: String,
    override val name: String,
) : Category