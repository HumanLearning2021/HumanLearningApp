package com.github.HumanLearning2021.HumanLearningApp.model

import android.net.Uri
import com.github.HumanLearning2021.HumanLearningApp.firestore.FirestoreCategorizedPicture
import com.github.HumanLearning2021.HumanLearningApp.firestore.FirestoreCategory
import com.github.HumanLearning2021.HumanLearningApp.offline.OfflineCategorizedPicture
import com.github.HumanLearning2021.HumanLearningApp.offline.OfflineCategory

object Converters {
    fun fromPicture(picture: FirestoreCategorizedPicture, uri: Uri): OfflineCategorizedPicture {
        return OfflineCategorizedPicture(picture.id, fromCategory(picture.category), uri)
    }

    fun fromCategory(category: FirestoreCategory): OfflineCategory {
        return OfflineCategory(category.id, category.name)
    }
}