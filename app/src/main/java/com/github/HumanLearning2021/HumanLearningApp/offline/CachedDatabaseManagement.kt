package com.github.HumanLearning2021.HumanLearningApp.offline

import com.github.HumanLearning2021.HumanLearningApp.model.*

class CachedDatabaseManagement internal constructor(
    private val db: DatabaseManagement, private val cache: PictureRepository
): DatabaseManagement by db {

    internal val cachedPictures: MutableMap<Id, CategorizedPicture> = mutableMapOf()

    override suspend fun getPicture(pictureId: Id): CategorizedPicture? {
        val uri = cache.retrievePicture(pictureId)
        return if (uri == null) {
            removeFromCache(pictureId)
            val fPic = db.getPicture(pictureId) ?: return null
            putIntoCache(fPic)
        } else {
            val cPic = cachedPictures[pictureId]
            if (cPic == null) {
                val fPic = db.getPicture(pictureId) ?: return null
                putIntoCache(fPic)
            } else {
                Converters.fromPicture(cPic, uri)
            }
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
        cachedPictures.remove(picture.id)
        cache.deletePicture(picture.id)
        val pic = db.getPicture(picture.id)
        if (pic != null) db.removePicture(pic)
    }

    private suspend fun putIntoCache(picture: CategorizedPicture): OfflineCategorizedPicture {
        val uri = cache.savePicture(picture)
        cachedPictures[picture.id] = picture
        return Converters.fromPicture(picture, uri)
    }

    private fun removeFromCache(pictureId: Id) {
        cachedPictures.remove(pictureId)
        try {
            cache.deletePicture(pictureId)
        } catch (e: DatabaseService.NotFoundException) {
            // Do nothing since it means that the cache already removed it itself
        }
    }
}
