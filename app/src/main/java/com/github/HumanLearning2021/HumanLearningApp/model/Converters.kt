package com.github.HumanLearning2021.HumanLearningApp.model

import android.net.Uri

object Converters {
    fun fromPicture(picture: CategorizedPicture, uri: Uri): CategorizedPicture {
        return CategorizedPicture(picture.id, fromCategory(picture.category), uri)
    }

    fun fromCategory(category: Category): Category {
        return Category(category.id, category.name)
    }
}