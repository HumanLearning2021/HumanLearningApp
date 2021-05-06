package com.github.HumanLearning2021.HumanLearningApp.model

import android.net.Uri
import com.github.HumanLearning2021.HumanLearningApp.offline.OfflineCategorizedPicture
import com.github.HumanLearning2021.HumanLearningApp.offline.OfflineCategory

object Converters {
    fun fromPicture(picture: CategorizedPicture, uri: Uri): OfflineCategorizedPicture {
        return OfflineCategorizedPicture(picture.id, fromCategory(picture.category), uri)
    }

    fun fromCategory(category: Category): OfflineCategory {
        return OfflineCategory(category.id, category.name)
    }
}