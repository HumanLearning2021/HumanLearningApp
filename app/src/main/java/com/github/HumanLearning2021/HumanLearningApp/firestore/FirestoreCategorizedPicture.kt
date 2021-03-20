package com.github.HumanLearning2021.HumanLearningApp.firestore

import android.app.Activity
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.github.HumanLearning2021.HumanLearningApp.model.CategorizedPicture
import com.github.HumanLearning2021.HumanLearningApp.model.Category
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

data class FirestoreCategorizedPicture(
    val path: String,
    override val category: Category,
    val url: String,
) : CategorizedPicture {
    override fun displayOn(activity: Activity, imageView: ImageView) {
        Glide.with(activity)
            .load(Firebase.storage.getReferenceFromUrl(url))
            .into(imageView)
    }
}