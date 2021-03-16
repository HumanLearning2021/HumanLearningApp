package com.github.HumanLearning2021.HumanLearningApp.model

import android.app.Activity
import android.widget.ImageView


/**
 * Interface representing a categorized picture
 */


interface CategorizedPicture {
    abstract val category:Category

    /**
     * A function that allows to display this image on an ImageView
     *
     *  @param imageView the ImageView on which to display the image
     *
     */

    abstract fun displayOn(activity: Activity, imageView: ImageView)
}