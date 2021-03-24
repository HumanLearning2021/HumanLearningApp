package com.github.HumanLearning2021.HumanLearningApp.model

import android.os.Parcelable

/**
 * An interface representing a category to which a CategorizedPicture can belong
 */
interface Category : Parcelable {
    val name: String
    val representativePicture: CategorizedPicture?
}