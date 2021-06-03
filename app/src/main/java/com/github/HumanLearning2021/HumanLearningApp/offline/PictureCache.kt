package com.github.HumanLearning2021.HumanLearningApp.offline

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import com.github.HumanLearning2021.HumanLearningApp.model.CategorizedPicture
import com.github.HumanLearning2021.HumanLearningApp.model.Id
import com.github.HumanLearning2021.HumanLearningApp.model.ImageDownloader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.util.*

/**
 * Allows to access picture from and save them to local storage
 * @property dbName name of the database the pictures belong to
 * @property context the application context
 * @property folder can be used to specify storage location, by default directory named after
 * the database in local storage
 */
class PictureCache(
    private val dbName: String, private val context: Context,
    private val folder: File = context.getDir(dbName, Context.MODE_PRIVATE)
) {

    private val imageDownloader = ImageDownloader(context)

    companion object {
        /**
         * Provides the picture cache stored in the application's cache folder.
         * Should be used when the intent is to cache pictures without having to deal with their
         * deletion.
         * @param dbName of the database the pictures belong to
         * @param context the application context
         */
        fun applicationPictureCache(
            dbName: String,
            context: Context
        ) = PictureCache(dbName, context, context.cacheDir)
    }

    /**
     * Saves a picture to local storage
     * @param picture to save
     * @return uri pointing to the saved picture
     */
    suspend fun savePicture(picture: CategorizedPicture): Uri {
        val file = File(folder, picture.id)
        with(imageDownloader) {
            picture.downloadTo(file)
        }
        return file.toUri()
    }

    /**
     * Saves a picture to local storage.
     * To be used to save pictures that are being added to the dataset.
     * @param uri pointing to the location of the image
     * @return id associated to the picture
     */
    suspend fun savePicture(uri: Uri): Id {
        return withContext(Dispatchers.IO) {
            val id = "${UUID.randomUUID()}"
            val file = File(folder, id)
            val path = uri.path
            path ?: throw IllegalArgumentException("Invalid uri provided")
            File(path).copyTo(file, true, DEFAULT_BUFFER_SIZE)
            id
        }
    }

    /**
     * Deletes the picture from storage
     * @param id of the picture to delete
     * @return true if successful, false otherwise
     * @throw IllegalArgumentException if no picture with this id are present
     */
    suspend fun deletePicture(id: Id): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                File("${folder.path}${File.pathSeparator}$id").delete()
            } catch (e: IOException) {
                throw IllegalArgumentException("There is not picture with id $id in the folder $dbName")
            }
        }
    }

    /**
     * Gets the location of a picture in storage
     * @param id of the picture to get
     * @return uri pointing to the picture, null if none was found
     */
    suspend fun retrievePicture(id: Id): Uri? {
        val file = File(folder, id)
        return withContext(Dispatchers.IO) {
            if (file.exists()) {
                Uri.fromFile(file)
            } else {
                null
            }
        }
    }

    /**
     * Deletes all the pictures from the storage directory associated to this instance
     * @return true if successful, false otherwise
     */
    suspend fun clear(): Boolean {
        return withContext(Dispatchers.IO) {
            folder.deleteRecursively()
        }
    }
}