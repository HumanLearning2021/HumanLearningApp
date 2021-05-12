package com.github.HumanLearning2021.HumanLearningApp.offline

import android.content.Context
import android.net.Uri
import androidx.camera.core.internal.IoConfig
import androidx.core.net.toUri
import com.github.HumanLearning2021.HumanLearningApp.model.CategorizedPicture
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.util.*

open class PictureRepository(
    private val dbName: String, private val context: Context,
    private val folder: File = context.getDir(dbName, Context.MODE_PRIVATE)
) {

    @Throws(Exception::class)
    suspend fun savePicture(picture: CategorizedPicture): Uri {
        return withContext(Dispatchers.IO) {
            val file = File(folder, picture.id)
            picture.copyTo(context, file)
            file.toUri()
        }
    }

    @Throws(IllegalArgumentException::class)
    suspend fun savePicture(uri: Uri): String {
        return withContext(Dispatchers.IO) {
            val id = "${UUID.randomUUID()}"
            val file = File(folder, id)
            val path = uri.path
            path ?: throw IllegalArgumentException("Invalid uri provided")
            File(path).copyTo(file, true, DEFAULT_BUFFER_SIZE)
            id
        }
    }

    @Throws(IllegalArgumentException::class)
    suspend fun deletePicture(id: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                File("${folder.path}${File.pathSeparator}$id").delete()
            } catch (e: IOException) {
                throw IllegalArgumentException("There is not picture with id $id in the folder $dbName")
            }
        }
    }

    suspend fun retrievePicture(id: String): Uri? {
        val file = File(folder, id)
        return withContext(Dispatchers.IO) {
            if (file.exists()) {
                Uri.fromFile(file)
            } else {
                null
            }
        }
    }

    suspend fun clear(): Boolean {
        return withContext(Dispatchers.IO) {
            folder.deleteRecursively()
        }
    }
}