package com.github.HumanLearning2021.HumanLearningApp.firestore

import com.github.HumanLearning2021.HumanLearningApp.model.CategorizedPicture
import com.github.HumanLearning2021.HumanLearningApp.model.Category
import com.github.HumanLearning2021.HumanLearningApp.model.Converters
import com.github.HumanLearning2021.HumanLearningApp.model.Id
import com.github.HumanLearning2021.HumanLearningApp.offline.OfflineCategorizedPicture
import com.github.HumanLearning2021.HumanLearningApp.offline.PictureRepository
import com.google.firebase.firestore.FirebaseFirestore

class CachedFirestoreDatabaseManagement internal constructor(
    dbName: String,
    firestore: FirebaseFirestore
): FirestoreDatabaseManagement(FirestoreDatabaseService(dbName, firestore)) {

    private lateinit var cache: PictureRepository
    private val cachedPictures: MutableMap<String, FirestoreCategorizedPicture> = mutableMapOf()

    override suspend fun getPicture(pictureId: Id): CategorizedPicture? {
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

    override suspend fun getRepresentativePicture(categoryId: Id): CategorizedPicture? {
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

    private fun removeFromCache(pictureId: Id) {
        cachedPictures.remove(pictureId)
        cache.deletePicture(pictureId)
    }
}
