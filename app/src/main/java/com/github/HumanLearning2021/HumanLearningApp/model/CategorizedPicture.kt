package com.github.HumanLearning2021.HumanLearningApp.model

import android.app.Activity
import android.content.Context
import android.os.Parcelable
import android.widget.ImageView

/**
 * Interface representing a categorized picture
 */

interface CategorizedPicture : Parcelable {
    val category: Category

    /**
     * A function that allows to display this image on an ImageView
     *
     * @param activity the activity to which the ImageView belongs
     *  @param imageView the ImageView on which to display the image
     *
     */
    fun displayOn(activity: Activity, imageView: ImageView)
}
