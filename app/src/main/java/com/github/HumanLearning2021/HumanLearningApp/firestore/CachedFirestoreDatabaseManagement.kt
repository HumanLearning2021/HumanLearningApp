package com.github.HumanLearning2021.HumanLearningApp.firestore

import android.content.Context
import android.net.Uri
import android.util.Log
import com.github.HumanLearning2021.HumanLearningApp.model.CategorizedPicture
import com.github.HumanLearning2021.HumanLearningApp.model.Category
import com.github.HumanLearning2021.HumanLearningApp.model.Converters
import com.github.HumanLearning2021.HumanLearningApp.offline.CachePictureRepository
import com.github.HumanLearning2021.HumanLearningApp.offline.OfflineCategorizedPicture
import com.github.HumanLearning2021.HumanLearningApp.offline.PictureRepository

class CachedFirestoreDatabaseManagement internal constructor(
    val dbName: String
): FirestoreDatabaseManagement(FirestoreDatabaseService(dbName)) {

    private lateinit var cache: PictureRepository
    private val cachedPictures: MutableMap<String, FirestoreCategorizedPicture> = mutableMapOf()

    override suspend fun getPicture(pictureId: Any): CategorizedPicture? {
        require(pictureId is String)
        val uri = cache.retrievePicture(pictureId)
        return if (uri == null) {
           removeFromCache(pictureId)
            val fPic = super.getPicture(pictureId) ?: return null
            putIntoCache(fPic as FirestoreCategorizedPicture)
        } else {
            Converters.fromPicture(cachedPictures[pictureId]!!, uri)
        }
    }

    override suspend fun getPicture(category: Category): CategorizedPicture? {
        val picIds = super.getPictureIds(category)
        return this.getPicture(picIds.random())
    }

    override suspend fun getRepresentativePicture(categoryId: Any): CategorizedPicture? {
        require(categoryId is String)
        val fPic = super.getRepresentativePicture(categoryId) ?: return null
        return this.getPicture(fPic.id)
    }

    override suspend fun removePicture(picture: CategorizedPicture) {
        require(picture is FirestoreCategorizedPicture)
        cachedPictures.remove(picture.id)
        cache.deletePicture(picture.id)
        super.removePicture(picture)
    }

    private suspend fun putIntoCache(picture: FirestoreCategorizedPicture): OfflineCategorizedPicture {
        val uri = cache.savePicture(picture)
        cachedPictures[picture.id] = picture
        return Converters.fromPicture(picture, uri)
    }

    private fun removeFromCache(pictureId: String) {
        cachedPictures.remove(pictureId)
        cache.deletePicture(pictureId)
    }
}
