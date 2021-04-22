package com.github.HumanLearning2021.HumanLearningApp.firestore

import android.app.Activity
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.github.HumanLearning2021.HumanLearningApp.model.CategorizedPicture
import com.github.HumanLearning2021.HumanLearningApp.model.Category
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.parcelize.Parcelize

@Parcelize
data class FirestoreCategorizedPicture internal constructor(
    override val id: String,
    override val path: String,
    override val category: FirestoreCategory,
    val url: String,
) : CategorizedPicture, FirestoreDocument {

    override fun displayOn(activity: Activity, imageView: ImageView) {
        Glide.with(activity)
            .load(Firebase.storage.getReferenceFromUrl(url))
            .into(imageView)
        // TODO uncomment if necessary (but interacts with learning activity)
        // and for accessibility it's better to make contentDescription human readable
        // imageView.contentDescription = url
    }
}