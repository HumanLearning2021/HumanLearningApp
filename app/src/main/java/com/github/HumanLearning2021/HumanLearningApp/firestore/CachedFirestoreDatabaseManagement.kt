package com.github.HumanLearning2021.HumanLearningApp.firestore

import android.content.Context
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import com.github.HumanLearning2021.HumanLearningApp.model.CategorizedPicture
import com.github.HumanLearning2021.HumanLearningApp.model.Category
import com.github.HumanLearning2021.HumanLearningApp.model.Converters
import com.github.HumanLearning2021.HumanLearningApp.offline.CachePictureRepository
import com.github.HumanLearning2021.HumanLearningApp.offline.OfflineCategorizedPicture

class CachedFirestoreDatabaseManagement internal constructor(
    dbName: String
): FirestoreDatabaseManagement(FirestoreDatabaseService(dbName)) {

    private val context: Context = ApplicationProvider.getApplicationContext()
    private val cache = CachePictureRepository(dbName, context)
    private val cachedPictures: MutableMap<String, FirestoreCategorizedPicture> = mutableMapOf()

    override suspend fun getPicture(pictureId: Any): CategorizedPicture? {
        require(pictureId is String)
        val uri = cache.retrievePicture(pictureId)
        return if (uri == null) {
            cachedPictures.remove(pictureId)
            val fPic = super.getPicture(pictureId) ?: return null
            putIntoCache(fPic as FirestoreCategorizedPicture)
        } else {
            Converters.fromPicture(cachedPictures[pictureId]!!, uri)
        }
    }

    override suspend fun removePicture(picture: CategorizedPicture) {
        require(picture is FirestoreCategorizedPicture)
        cache.deletePicture(picture.id)
        super.removePicture(picture)
    }

    override suspend fun putPicture(picture: Uri, category: Category): FirestoreCategorizedPicture {
        val res = super.putPicture(picture, category)
        putIntoCache(res)
        return res
    }

    private suspend fun putIntoCache(picture: FirestoreCategorizedPicture): OfflineCategorizedPicture {
        val uri = cache.savePicture(picture)
        cachedPictures[picture.id] = picture
        return Converters.fromPicture(picture, uri)
    }
}
