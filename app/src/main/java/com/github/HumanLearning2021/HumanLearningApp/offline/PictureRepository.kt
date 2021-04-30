package com.github.HumanLearning2021.HumanLearningApp.offline

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import com.github.HumanLearning2021.HumanLearningApp.model.CategorizedPicture
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.util.*

open class PictureRepository(private val dbName: String, private val context: Context,
                        private val folder: File = context.getDir(dbName, Context.MODE_PRIVATE)
) {

    @Throws(Exception::class)
     fun savePicture(picture: CategorizedPicture): Uri {
        val file = File(folder, picture.id)
        picture.copyTo(context, file)
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
            File("${folder.path}${File.pathSeparator}$id").delete()
        } catch (e: IOException) {
            throw IllegalArgumentException("There is not picture with id $id in the folder $dbName")
        }
    }

    fun retrievePicture(id: String): Uri? {
        val file = File(folder, id)
        return if (file.exists()) {
            Uri.fromFile(file)
        } else {
            null
        }
    }

    fun clear(): Boolean {
        return folder.deleteRecursively()
    }
}