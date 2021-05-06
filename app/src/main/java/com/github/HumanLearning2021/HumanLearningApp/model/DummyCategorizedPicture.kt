package com.github.HumanLearning2021.HumanLearningApp.model

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.widget.ImageView
import androidx.annotation.MainThread
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.io.File
import java.io.InputStream
import java.lang.IllegalArgumentException

/**
 * A picture part of the dummy data set.
 */
@Parcelize
data class DummyCategorizedPicture(override val id: Id, override val category: Category, val picture: Uri
) : CategorizedPicture {

    override fun displayOn(activity: Activity, imageView: ImageView) {
        Glide.with(activity).load(picture).into(imageView)
    }

    /**
     * copy image data to a file.
     */
    override fun copyTo(context: Context, dest: File) {
        context.contentResolver.openInputStream(picture)!!.use { src ->
            dest.outputStream().use {
                src.copyTo(it)
            }
        }
    }
}
