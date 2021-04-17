package com.github.HumanLearning2021.HumanLearningApp.offline

import android.app.Activity
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import com.github.HumanLearning2021.HumanLearningApp.model.CategorizedPicture
import com.github.HumanLearning2021.HumanLearningApp.model.Category
import kotlinx.parcelize.Parcelize

@Parcelize
class OfflineCategorizedPicture(override val id: String, override val category: Category, val picture: Uri) : CategorizedPicture {

    override fun displayOn(activity: Activity, imageView: ImageView) {
        val inputStream = activity.contentResolver.openInputStream(picture)
        val drawable = Drawable.createFromStream(inputStream, picture.toString())
        imageView.setImageDrawable(drawable)
        imageView.tag = drawable
    }
}