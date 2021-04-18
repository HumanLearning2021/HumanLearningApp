package com.github.HumanLearning2021.HumanLearningApp.offline

import android.content.Context
import android.graphics.Path
import android.net.Uri
import androidx.core.net.toUri
import androidx.test.core.app.ApplicationProvider
import com.github.HumanLearning2021.HumanLearningApp.firestore.FirestoreCategorizedPicture
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import java.io.File
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.lang.NullPointerException
import java.util.*

data class PictureRepository(val context: Context, val dbName: String) {

    private val folder = context.getDir(dbName, Context.MODE_PRIVATE)

    @Throws(Exception::class)
    suspend fun savePicture(picture: FirestoreCategorizedPicture): String {
        val file = File(folder, picture.id)
        val task = Firebase.storage.getReferenceFromUrl(picture.url).getFile(file).await()
        task.error ?: throw task.error!!
        return picture.id
    }

    @Throws(IllegalArgumentException::class)
    fun savePicture(uri: Uri): String {
        val id = "${UUID.randomUUID()}"
        val file = File(folder, id)
        val path = uri.path
        path ?: throw IllegalArgumentException("Invalid uri provided")
        File(path).copyTo(file, true, DEFAULT_BUFFER_SIZE)
        return id
    }

    @Throws(IllegalArgumentException::class)
    fun deletePicture(id: String): Boolean {
        return File(folder, id).delete()
    }

    @Throws(IllegalArgumentException::class)
    fun retrievePicture(id: String): Uri {
        return try {
            File(folder, id).toUri()
        } catch (e: NullPointerException) {
            throw IllegalArgumentException("There is not picture with id $id in the folder $dbName")
        }
    }
}