package com.github.HumanLearning2021.HumanLearningApp.model

import android.app.Activity
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import com.github.HumanLearning2021.HumanLearningApp.R
import java.io.Serializable
import java.lang.IllegalArgumentException

/**
 * A picture part of the dummy data set. Can be of any of the following categories: "fork",
 * "knife", "spoon"
 */
data class DummyCategorizedPicture(override val category: Category, val picture: Uri) : CategorizedPicture {

    override fun displayOn(activity: Activity, imageView: ImageView) {
        if(category !is DummyCategory) throw IllegalArgumentException("provide a dummy category " +
                "to the class constructor")
        imageView.setImageDrawable(Drawable.createFromPath(picture.path))
    }
}