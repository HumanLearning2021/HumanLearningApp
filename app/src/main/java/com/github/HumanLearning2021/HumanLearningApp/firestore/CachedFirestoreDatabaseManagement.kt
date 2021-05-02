package com.github.HumanLearning2021.HumanLearningApp.firestore

import com.github.HumanLearning2021.HumanLearningApp.model.*
import com.github.HumanLearning2021.HumanLearningApp.offline.OfflineCategorizedPicture
import com.github.HumanLearning2021.HumanLearningApp.offline.PictureRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.parcelize.Parcelize

class CachedFirestoreDatabaseManagement internal constructor(
    val db: FirestoreDatabaseManagement
): DatabaseManagement by db {

    private lateinit var cache: PictureRepository
    private val cachedPictures: MutableMap<String, FirestoreCategorizedPicture> = mutableMapOf()

    override suspend fun getPicture(pictureId: Id): CategorizedPicture? {
        val uri = cache.retrievePicture(pictureId)
        return if (uri == null) {
           removeFromCache(pictureId)
            val fPic = getPicture(pictureId) ?: return null
            putIntoCache(fPic as FirestoreCategorizedPicture)
        } else {
            Converters.fromPicture(cachedPictures[pictureId]!!, uri)
        }
    }

    override suspend fun getPicture(category: Category): CategorizedPicture? {
        val picIds = db.getPictureIds(category)
        return this.getPicture(picIds.random())
    }

    override suspend fun getRepresentativePicture(categoryId: Id): CategorizedPicture? {
        val fPic = db.getRepresentativePicture(categoryId) ?: return null
        return this.getPicture(fPic.id)
    }

    override suspend fun removePicture(picture: CategorizedPicture) {
        require(picture is FirestoreCategorizedPicture)
        cachedPictures.remove(picture.id)
        cache.deletePicture(picture.id)
        db.removePicture(picture)
    }

    private suspend fun putIntoCache(picture: FirestoreCategorizedPicture): OfflineCategorizedPicture {
        val uri = cache.savePicture(picture)
        cachedPictures[picture.id] = picture
        return Converters.fromPicture(picture, uri)
    }

    private fun removeFromCache(pictureId: Id) {
        cachedPictures.remove(pictureId)
        cache.deletePicture(pictureId)
    }
}
