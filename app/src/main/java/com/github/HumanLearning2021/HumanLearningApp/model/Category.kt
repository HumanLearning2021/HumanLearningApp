package com.github.HumanLearning2021.HumanLearningApp.model

import java.io.Serializable

/**
 * An interface representing a category to which a CategorizedPicture can belong
 */
interface Category : Serializable{
    val name: String
    val representativePicture: CategorizedPicture?
}