package com.github.HumanLearning2021.HumanLearningApp.model

import android.app.Activity
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.lang.IllegalArgumentException

/**
 * A picture part of the dummy data set.
 */
@Parcelize
data class DummyCategorizedPicture(override val id: String, override val category: Category, val picture: Uri
) : CategorizedPicture {

    override fun displayOn(activity: Activity, imageView: ImageView) {
        Glide.with(activity).load(picture).into(imageView)
    }
}
