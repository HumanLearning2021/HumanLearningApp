package com.github.HumanLearning2021.HumanLearningApp.model

import android.app.Activity
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import kotlinx.parcelize.Parcelize
import java.lang.IllegalArgumentException

/**
 * A picture part of the dummy data set. Can be of any of the following categories: "fork",
 * "knife", "spoon"
 */
@Parcelize
class DummyCategorizedPicture(override val category: Category, val picture: Uri) : CategorizedPicture {

    override fun displayOn(activity: Activity, imageView: ImageView) {
        if (category !is DummyCategory) throw IllegalArgumentException("provide a dummy category " +
                "to the class constructor")
        val inputStream = activity.contentResolver.openInputStream(picture)
        val drawable = Drawable.createFromStream(inputStream, picture.toString())
        imageView.setImageDrawable(drawable)
        imageView.setTag(drawable)
    }

    // Have to override these 2 and not make it a data class because of DummyDatabaseService's initialization:
    // A category depends on a rep pic; and a rep pic depends on a category. Making CategorizedPicture
    // a data class led to an endless loop. If someone has a more elegant solution, would be nice,
    // but otherwise it's only the Dummy implementation
    override fun equals(other: Any?): Boolean {
        return other is DummyCategorizedPicture && other.category == category && other.picture == picture
    }

    override fun hashCode(): Int {
        return 17 + 31*category.hashCode() + 31*picture.hashCode()
    }
}