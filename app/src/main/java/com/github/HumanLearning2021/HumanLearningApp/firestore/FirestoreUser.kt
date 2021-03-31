package com.github.HumanLearning2021.HumanLearningApp.firestore

import com.github.HumanLearning2021.HumanLearningApp.model.User
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FirestoreUser(
    override val path: String,
    override val displayName: String?,
    override val email: String?,
    override val uid: String,
    override val type: User.Type,
) : FirestoreDocument, User