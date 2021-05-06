package com.github.HumanLearning2021.HumanLearningApp.offline

import android.app.Activity
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.github.HumanLearning2021.HumanLearningApp.model.CategorizedPicture
import kotlinx.parcelize.Parcelize

@Parcelize
class OfflineCategorizedPicture(override val id: String, override val category: OfflineCategory, val picture: Uri) : CategorizedPicture {

    override fun displayOn(activity: Activity, imageView: ImageView) {
        Glide.with(activity).load(picture).into(imageView)
    }
}