package com.github.HumanLearning2021.HumanLearningApp.offline

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import com.github.HumanLearning2021.HumanLearningApp.model.CategorizedPicture
import com.github.HumanLearning2021.HumanLearningApp.model.Id
import java.io.File
import java.io.IOException
import java.util.*

/**
 * Allows access to the local storage to store and load pictures from it.
 *
 * @property dbName: name of the database whose pictures we want to access
 * @property context: the application context
 * @property folder: folder where the pictures are stored
 * @constructor creates an object from which we can access the offline picture storage for the specified database
 */
open class PictureStorage(
    private val dbName: String, private val context: Context,
    private val folder: File = context.getDir(dbName, Context.MODE_PRIVATE)
) {

    /**
     * Saves a picture to local storage
     *
     * @param picture: picture to save
     * @return Uri of the saved picture
     */
    fun savePicture(picture: CategorizedPicture): Uri {
        val file = File(folder, picture.id)
        picture.copyTo(context, file)
        return file.toUri()
    }

    /**
     * Saves a picture to local storage
     *
     * @param uri: Uri from the picture to save
     * @return id of the saved picture
     */
    fun savePicture(uri: Uri): String {
        val id = "${UUID.randomUUID()}"
        val file = File(folder, id)
        val path = uri.path
        path ?: throw IllegalArgumentException("Invalid uri provided")
        File(path).copyTo(file, true, DEFAULT_BUFFER_SIZE)
        return id
    }

    /**
     * Deletes a picture from local storage
     *
     * @param id: id of the picture to delete
     * @return true if deletion was successful, false otherwise
     */
    fun deletePicture(id: Id): Boolean {
        return try {
            File("${folder.path}${File.pathSeparator}$id").delete()
        } catch (e: IOException) {
            throw IllegalArgumentException("There is not picture with id $id in the folder $dbName")
        }
    }

    /**
     * Get the Uri of a picture
     *
     * @param id: id of the picture to retrieve
     * @return Uri of the desired picture, null if there is no picture with the specified id in storage
     */
    fun retrievePicture(id: Id): Uri? {
        val file = File(folder, id)
        return if (file.exists()) {
            Uri.fromFile(file)
        } else {
            null
        }
    }

    /**
     * Deletes the entire content of the local picture storage for this database
     *
     * @return true if successful, false otherwise
     */
    fun clear(): Boolean {
        return folder.deleteRecursively()
    }
}