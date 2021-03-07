package com.github.HumanLearning2021.HumanLearningApp.Model

import android.graphics.drawable.Drawable


/**
 * An interface representing the part of the model interacting with data sets
 */
interface DataSetInterface {
    suspend fun getPicture(categoryString: String): CategorizedPicture
}