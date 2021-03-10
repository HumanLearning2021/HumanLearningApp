package com.github.HumanLearning2021.HumanLearningApp.Model

import android.graphics.drawable.Drawable



interface DataSetInterface {
    fun getPicture(categoryString: String): CategorizedPicture
}