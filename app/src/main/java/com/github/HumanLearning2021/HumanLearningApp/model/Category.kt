package com.github.HumanLearning2021.HumanLearningApp.model

import android.os.Parcelable

/**
 * An interface representing a category to which a CategorizedPicture can belong
 * id is used to uniquely identify a Category
 */
interface Category : Parcelable {
    val id: Any
    val name: String
    val representativePicture: CategorizedPicture?
}