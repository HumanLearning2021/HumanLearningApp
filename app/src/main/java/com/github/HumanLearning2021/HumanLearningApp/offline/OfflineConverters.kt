package com.github.HumanLearning2021.HumanLearningApp.offline

import com.github.HumanLearning2021.HumanLearningApp.model.CategorizedPicture
import com.github.HumanLearning2021.HumanLearningApp.model.Category
import com.github.HumanLearning2021.HumanLearningApp.model.Dataset
import com.github.HumanLearning2021.HumanLearningApp.model.User
import com.github.HumanLearning2021.HumanLearningApp.room.*

object OfflineConverters {
    suspend fun fromPicture(
        picture: RoomPicture,
        categoryDao: CategoryDao
    ): CategorizedPicture {
        val cat = fromCategory(categoryDao.loadById(picture.categoryId)!!)
        return CategorizedPicture(picture.pictureId, cat, picture.uri)
    }

    suspend fun fromPicture(
        picture: RoomRepresentativePicture,
        categoryDao: CategoryDao
    ): CategorizedPicture? {
        val pic = picture.picture ?: return null
        val cat = fromCategory(categoryDao.loadById(pic.categoryId)!!)
        return CategorizedPicture(pic.pictureId, cat, pic.uri)
    }

    fun fromCategory(category: RoomCategory): Category {
        return Category(category.categoryId, category.name)
    }

    fun fromCategory(category: Category): RoomCategory {
        return RoomCategory(category.id, category.name)
    }

    fun fromDataset(dataset: RoomDataset): Dataset {
        val cats = dataset.categories.map { c -> fromCategory(c) }.toSet()
        return Dataset(
            dataset.datasetWithoutCategories.datasetId,
            dataset.datasetWithoutCategories.name,
            cats
        )
    }

    fun fromUser(user: RoomUser): User {
        return User(
            displayName = user.displayName,
            email = user.email,
            user.userId,
            user.type,
            user.isAdmin,
        )
    }
}