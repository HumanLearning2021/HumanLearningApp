package com.github.HumanLearning2021.HumanLearningApp.Model

import android.graphics.drawable.Drawable
import android.widget.ImageView

// TODO: find better name
abstract class CategorizedPicture(val category: Category){
    abstract fun displayOn(imageView: ImageView)

    override fun equals(other: Any?): Boolean {
        return (other is CategorizedPicture) && category == other.category
    }

    override fun hashCode(): Int {
        return 17 + 31 * category.hashCode()
    }
}