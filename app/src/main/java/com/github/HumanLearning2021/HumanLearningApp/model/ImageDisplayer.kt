package com.github.HumanLearning2021.HumanLearningApp.model

import android.app.Activity
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

/**
 * Utility to handle displaying images. Supports Google Storage.
 */
interface ImageDisplayer {
    fun (CategorizedPicture).displayOn(target: ImageView)
}

class DefaultImageDisplayer constructor(
    private val currentActivity: Activity,
) : ImageDisplayer {
    /**
     * A function that allows to display this image on an ImageView
     *
     *  @param imageView the ImageView on which to display the image
     *
     */
    override fun (CategorizedPicture).displayOn(target: ImageView) {
        val requestManager = Glide.with(currentActivity)
        val requestBuilder = if (picture.scheme == "gs")
            requestManager.load(Firebase.storage.getReferenceFromUrl(picture.toString()))
        else
            requestManager.load(picture)
        requestBuilder.into(target)
    }
}

@InstallIn(ActivityComponent::class)
@Module
object ImageDisplayerModule {
    @Provides
    fun provideImageDisplayer(currentActivity: Activity): ImageDisplayer =
        DefaultImageDisplayer(currentActivity)
}

object NoopImageDisplayer : ImageDisplayer {
    override fun CategorizedPicture.displayOn(target: ImageView) {
        // nothing to do!
    }
}

