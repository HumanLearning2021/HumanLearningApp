package com.github.HumanLearning2021.HumanLearningApp.offline

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import com.github.HumanLearning2021.HumanLearningApp.firestore.FirestoreCategorizedPicture
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.lang.NullPointerException
import java.util.*

open class PictureRepository(private val dbName: String, private val context: Context,
                        private val folder: File = context.getDir(dbName, Context.MODE_PRIVATE)
) {

    @Throws(Exception::class)
    suspend fun savePicture(picture: FirestoreCategorizedPicture): Uri {
        val file = File(folder, picture.id)
        val task = Firebase.storage.getReferenceFromUrl(picture.url).getFile(file).await()
        if (task.error != null) throw task.error!!
        return file.toUri()
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
        return try {
            File(folder, id).delete()
        } catch (e: IOException) {
            throw IllegalArgumentException("There is not picture with id $id in the folder $dbName")
        }
    }

    fun retrievePicture(id: String): Uri? {
        return try {
            File(folder, id).toUri()
        } catch (e: NullPointerException) {
            null
        }
    }

    fun clear(): Boolean {
        return folder.deleteRecursively()
    }
}