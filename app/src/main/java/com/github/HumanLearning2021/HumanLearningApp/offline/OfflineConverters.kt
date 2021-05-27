package com.github.HumanLearning2021.HumanLearningApp.offline

import com.github.HumanLearning2021.HumanLearningApp.model.CategorizedPicture
import com.github.HumanLearning2021.HumanLearningApp.model.Category
import com.github.HumanLearning2021.HumanLearningApp.model.Dataset
import com.github.HumanLearning2021.HumanLearningApp.model.User
import com.github.HumanLearning2021.HumanLearningApp.room.*

/**
 * Object containing methods to convert to and from Room entities
 */
object OfflineConverters {

    /**
     * Convert a RoomPicture to a CategorizedPicture
     * @param picture to convert
     * @param categoryDao of the Room database the picture is from
     * @return the resulting CategorizedPicture
     */
    suspend fun fromPicture(
        picture: RoomPicture,
        categoryDao: CategoryDao
    ): CategorizedPicture {
        val cat = fromCategory(categoryDao.loadById(picture.categoryId)!!)
        return CategorizedPicture(picture.pictureId, cat, picture.uri)
    }

    /**
     * Convert a RoomRepresentativePicture to a CategorizedPicture
     * @param picture to convert
     * @param categoryDao of the Room database the picture is from
     * @return the resulting CategorizedPicture
     */
    suspend fun fromPicture(
        picture: RoomRepresentativePicture,
        categoryDao: CategoryDao
    ): CategorizedPicture {
        val cat = fromCategory(categoryDao.loadById(picture.picture.categoryId)!!)
        return CategorizedPicture(picture.picture.pictureId, cat, picture.picture.uri)
    }

    /**
     * Convert a RoomCategory to a Category
     * @param category to convert
     * @return the resulting Category
     */
    fun fromCategory(category: RoomCategory): Category {
        return Category(category.categoryId, category.name)
    }

    /**
     * Convert a Category to a RoomCategory
     * @param category to convert
     * @return the resulting RoomCategory
     */
    fun fromCategory(category: Category): RoomCategory {
        return RoomCategory(category.id, category.name)
    }

    /**
     * Convert a RoomDataset to a Dataset
     * @param dataset to convert
     * @return the resulting Dataset
     */
    fun fromDataset(dataset: RoomDataset): Dataset {
        val cats = dataset.categories.map { c -> fromCategory(c) }.toSet()
        return Dataset(
            dataset.datasetWithoutCategories.datasetId,
            dataset.datasetWithoutCategories.name,
            cats
        )
    }

    /**
     * Convert a RoomUser to a User
     * @param user to convert
     * @return the resulting User
     */
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