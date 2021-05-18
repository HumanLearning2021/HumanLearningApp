package com.github.HumanLearning2021.HumanLearningApp.offline

import com.github.HumanLearning2021.HumanLearningApp.model.CategorizedPicture
import com.github.HumanLearning2021.HumanLearningApp.model.Converters
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
    private val db: DatabaseService, private val cache: PictureRepository
) : DatabaseService by db {

    internal val cachedPictures: MutableMap<Id, CategorizedPicture> = mutableMapOf()

    override suspend fun getPicture(pictureId: Id): CategorizedPicture? =
        withContext(Dispatchers.IO) {
            val uri = cache.retrievePicture(pictureId)
            if (uri == null) {
                removeFromCache(pictureId)
                db.getPicture(pictureId)?.let { putIntoCache(it) }
            } else {
                val cPic = cachedPictures[pictureId]
                if (cPic == null) {
                    db.getPicture(pictureId)?.let { putIntoCache(it) }
                } else {
                    Converters.fromPicture(cPic, uri)
                }
            }
        }

    override suspend fun getRepresentativePicture(categoryId: Id): CategorizedPicture? =
        withContext(Dispatchers.IO) {
            db.getRepresentativePicture(categoryId)?.id?.let { id ->
                getRepresentativePictureFromCache(id)
            } ?: let {
                db.getRepresentativePicture(categoryId)?.let { cPic ->
                    putIntoCache(cPic)
                }
            }
        }

    override suspend fun removePicture(picture: CategorizedPicture) = withContext(Dispatchers.IO) {
        cachedPictures.remove(picture.id)
        cache.deletePicture(picture.id)
        val pic = db.getPicture(picture.id)
        if (pic != null) db.removePicture(pic)
    }

    private suspend fun putIntoCache(picture: CategorizedPicture): OfflineCategorizedPicture =
        withContext(Dispatchers.IO) {
            val uri = cache.savePicture(picture)
            cachedPictures[picture.id] = picture
            Converters.fromPicture(picture, uri)
        }

    private suspend fun removeFromCache(pictureId: Id) = withContext(Dispatchers.IO) {
        cachedPictures.remove(pictureId)
        try {
            cache.deletePicture(pictureId)
        } catch (e: DatabaseService.NotFoundException) {
            // Do nothing since it means that the cache already removed it itself
        }
    }

    private suspend fun getRepresentativePictureFromCache(pictureId: Id): CategorizedPicture? =
        withContext(Dispatchers.IO) {
            cache.retrievePicture(pictureId)?.let { uri ->
                cachedPictures[pictureId]?.let { cPic ->
                    Converters.fromPicture(cPic, uri)
                } ?: db.getPicture(pictureId)?.let { pic -> putIntoCache(pic) }
            } ?: run {
                removeFromCache(pictureId)
                db.getPicture(pictureId)?.let { pic -> putIntoCache(pic) }
            }
        }
}
