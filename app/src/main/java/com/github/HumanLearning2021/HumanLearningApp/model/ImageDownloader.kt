package com.github.HumanLearning2021.HumanLearningApp.model

import android.content.Context
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

/**
 * Utility to handle downloading images. Supports Google Storage.
 * @property context the application context
 */
class ImageDownloader @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    /**
     * Function to download a the underlying picture of a CategorizedPicture
     * @param dest the download destination
     */
    suspend fun (CategorizedPicture).downloadTo(dest: File) {
        if (picture.scheme == "gs")
            Firebase.storage.getReferenceFromUrl(picture.toString()).getFile(dest).await()
        else
            withContext(Dispatchers.IO) {
                context.contentResolver.openInputStream(picture)!!.use { src ->
                    dest.outputStream().use {
                        src.copyTo(it)
                    }
                }
            }
    }
}