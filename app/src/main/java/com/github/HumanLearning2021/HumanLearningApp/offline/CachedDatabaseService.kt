package com.github.HumanLearning2021.HumanLearningApp.offline

import android.net.Uri
import com.github.HumanLearning2021.HumanLearningApp.model.CategorizedPicture
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseService
import com.github.HumanLearning2021.HumanLearningApp.model.Id
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Decorator for a DatabaseService which adds caching for the pictures
 *
 * @property db: database management to decorate
 * @property cache: PictureStorage of the cache
 * @constructor the specified DatabaseManagement decorated as a cache
 */
class CachedDatabaseService internal constructor(
    private val db: DatabaseService, private val cache: PictureCache
) : DatabaseService by db {

    internal val cachedPictures: MutableMap<Id, CategorizedPicture> = mutableMapOf()

    override suspend fun getPicture(pictureId: Id): CategorizedPicture? =
        withContext(Dispatchers.IO) {
            cache.retrievePicture(pictureId)?.let { uri ->
                getFromCache(pictureId, uri)
            } ?: updateCache(pictureId)
        }

    override suspend fun getRepresentativePicture(categoryId: Id): CategorizedPicture? =
        withContext(Dispatchers.IO) {
            db.getRepresentativePicture(categoryId)?.id?.let { id ->
                getPicture(id)
            } ?: let {
                db.getRepresentativePicture(categoryId)?.let { cPic ->
                    putIntoCache(cPic)
                }
            }
        }

    override suspend fun removePicture(picture: CategorizedPicture): Unit =
        withContext(Dispatchers.IO) {
            cachedPictures.remove(picture.id)
            cache.deletePicture(picture.id)
            db.getPicture(picture.id)?.let { db.removePicture(it) }
        }

    private suspend fun putIntoCache(picture: CategorizedPicture): CategorizedPicture =
        withContext(Dispatchers.IO) {
            val uri = cache.savePicture(picture)
            cachedPictures[picture.id] = picture
            CategorizedPicture(picture.id, picture.category, uri)
        }

    private suspend fun removeFromCache(pictureId: Id) = withContext(Dispatchers.IO) {
        cachedPictures.remove(pictureId)
        try {
            cache.deletePicture(pictureId)
        } catch (e: DatabaseService.NotFoundException) {
            // Do nothing since it means that the cache already removed it itself
        }
    }

    private suspend fun getFromCache(pictureId: Id, uri: Uri): CategorizedPicture? =
        withContext(Dispatchers.IO) {
            cachedPictures[pictureId]?.let { cPic ->
                CategorizedPicture(cPic.id, cPic.category, uri)
            } ?: db.getPicture(pictureId)?.let { putIntoCache(it) }
        }

    private suspend fun updateCache(pictureId: Id): CategorizedPicture? =
        withContext(Dispatchers.IO) {
            removeFromCache(pictureId)
            db.getPicture(pictureId)?.let { cPic -> putIntoCache(cPic) }
        }
}
