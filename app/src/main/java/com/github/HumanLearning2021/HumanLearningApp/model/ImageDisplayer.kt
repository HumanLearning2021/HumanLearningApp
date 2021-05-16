package com.github.HumanLearning2021.HumanLearningApp.model

import android.app.Activity
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


/**
 * Utility to handle displaying images. Supports Google Storage.
 */
interface ImageDisplayer {
    suspend fun (CategorizedPicture).displayOn(target: ImageView)
}

private class RequestListenerAdapter(val cont: Continuation<Unit>) : RequestListener<Drawable> {
    private var done: Boolean = false

    override fun onLoadFailed(
        e: GlideException?,
        model: Any?,
        target: Target<Drawable>?,
        isFirstResource: Boolean
    ): Boolean {
        Log.e(DefaultImageDisplayer::class.qualifiedName, "load failed", e)
        if (!done)
            cont.resumeWithException(e ?: Exception("unknown error"))
        done = true
        return false
    }

    override fun onResourceReady(
        resource: Drawable?,
        model: Any?,
        target: Target<Drawable>?,
        dataSource: DataSource?,
        isFirstResource: Boolean
    ): Boolean {
        if (!done)
            cont.resume(Unit)
        done = true
        return false
    }

}

class DefaultImageDisplayer constructor(
    private val currentActivity: Activity,
) : ImageDisplayer {
    /**
     * A function that allows to display this image on an ImageView
     *
     *  @param target the ImageView on which to display the image
     *
     */
    override suspend fun (CategorizedPicture).displayOn(target: ImageView) {
        val requestManager = Glide.with(currentActivity)
        val requestBuilder = if (picture.scheme == "gs")
            requestManager.load(Firebase.storage.getReferenceFromUrl(picture.toString()))
        else
            requestManager.load(picture)
        suspendCoroutine<Unit> { cont ->
            requestBuilder.listener(RequestListenerAdapter(cont)).into(target)
        }
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
    override suspend fun CategorizedPicture.displayOn(target: ImageView) {
        // nothing to do!
    }
}

