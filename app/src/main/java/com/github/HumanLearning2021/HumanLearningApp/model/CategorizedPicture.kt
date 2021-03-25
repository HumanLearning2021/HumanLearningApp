package com.github.HumanLearning2021.HumanLearningApp.model

import android.app.Activity
import android.widget.ImageView
import java.io.Serializable


/**
 * Interface representing a categorized picture
 */


interface CategorizedPicture : Serializable{
    val category:Category

    /**
     * A function that allows to display this image on an ImageView
     *
     * @param activity the activity to which the ImageView belongs
     *  @param imageView the ImageView on which to display the image
     *
     */
    fun displayOn(activity: Activity, imageView: ImageView)
}
