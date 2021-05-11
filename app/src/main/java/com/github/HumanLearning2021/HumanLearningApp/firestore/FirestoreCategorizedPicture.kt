package com.github.HumanLearning2021.HumanLearningApp.firestore

import android.app.Activity
import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.github.HumanLearning2021.HumanLearningApp.model.CategorizedPicture
import com.github.HumanLearning2021.HumanLearningApp.model.Id
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.parcelize.Parcelize
import java.io.File

@Parcelize
data class FirestoreCategorizedPicture internal constructor(
    override val id: Id,
    override val category: FirestoreCategory,
    val url: String,
) : CategorizedPicture {

    override fun displayOn(activity: Activity, imageView: ImageView) {
        Glide.with(activity)
            .load(Firebase.storage.getReferenceFromUrl(url))
            .into(imageView)
    }

    /**
     * copy image data to a file.
     */
    override fun copyTo(context: Context, dest: File) {
        runBlocking {
            Firebase.storage.getReferenceFromUrl(url).getFile(dest).await()
        }
    }
}