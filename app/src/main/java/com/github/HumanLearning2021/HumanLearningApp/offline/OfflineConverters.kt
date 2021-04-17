package com.github.HumanLearning2021.HumanLearningApp.offline

import com.github.HumanLearning2021.HumanLearningApp.model.Category
import com.github.HumanLearning2021.HumanLearningApp.room.*

object OfflineConverters {
    fun fromPicture(picture: RoomPicture, categoryDao: CategoryDao): OfflineCategorizedPicture {
        val cat = fromCategory(categoryDao.loadById(picture.categoryId))
        return OfflineCategorizedPicture(picture.pictureId, cat, picture.uri)
    }

    fun fromPicture(picture: RoomRepresentativePicture, categoryDao: CategoryDao): OfflineCategorizedPicture {
        val cat = fromCategory(categoryDao.loadById(picture.picture.categoryId))
        return OfflineCategorizedPicture(picture.picture.pictureId, cat, picture.picture.uri)
    }

    fun fromCategory(category: RoomCategory): OfflineCategory {
        return OfflineCategory(category.categoryId, category.name)
    }

    fun fromCategory(category: Category): RoomCategory {
        return RoomCategory(category.id as String, category.name)
    }

    fun fromDataset(dataset: RoomDataset, categoryDao: CategoryDao): OfflineDataset {
        val cats = dataset.categories.map { c -> fromCategory(c) }.toSet()
        return OfflineDataset(dataset.datasetWithoutCategories.datasetId, dataset.datasetWithoutCategories.name, cats)
    }

    fun fromUser(user: RoomUser): OfflineUser {
        return OfflineUser(user.type, user.userId, user.displayName, user.email)
    }
}