package com.github.HumanLearning2021.HumanLearningApp.firestore

import com.github.HumanLearning2021.HumanLearningApp.model.Dataset
import kotlinx.parcelize.Parcelize

@Parcelize
class FirestoreDataset(
    override val path: String,
    override val id: String,
    override val name: String,
    override val categories: Set<FirestoreCategory>
) : Dataset, FirestoreDocument
