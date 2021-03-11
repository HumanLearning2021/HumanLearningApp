package com.github.HumanLearning2021.HumanLearningApp.Model

import android.graphics.drawable.Drawable
import android.widget.ImageView


/**
 * Abstract class representing a categorized picture
 *
 * @param category the category to which this picture belongs
 */
abstract class CategorizedPicture(val category: Category){

    /**
     * A function that allows to display this image on an ImageView
     *
     *  @param imageView the ImageView on which to display the image
     */
    abstract fun displayOn(imageView: ImageView)

    override fun equals(other: Any?): Boolean {
        return (other is CategorizedPicture) && category == other.category
    }

    override fun hashCode(): Int {
        return 17 + 31 * category.hashCode()
    }
}