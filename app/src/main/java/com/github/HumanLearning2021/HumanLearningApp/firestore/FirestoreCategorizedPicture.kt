package com.github.HumanLearning2021.HumanLearningApp.firestore

import android.app.Activity
import android.os.Parcel
import android.os.Parcelable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.github.HumanLearning2021.HumanLearningApp.model.CategorizedPicture
import com.github.HumanLearning2021.HumanLearningApp.model.Category
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

data class FirestoreCategorizedPicture internal constructor(
    override val path: String,
    override val category: Category,
    val url: String,
) : CategorizedPicture, FirestoreDocument {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readParcelable(Category::class.java.classLoader)!!,
        parcel.readString()!!
    )

    override fun displayOn(activity: Activity, imageView: ImageView) {
        Glide.with(activity)
            .load(Firebase.storage.getReferenceFromUrl(url))
            .into(imageView)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(path)
        parcel.writeParcelable(category, flags)
        parcel.writeString(url)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FirestoreCategorizedPicture> {
        override fun createFromParcel(parcel: Parcel): FirestoreCategorizedPicture {
            return FirestoreCategorizedPicture(parcel)
        }

        override fun newArray(size: Int): Array<FirestoreCategorizedPicture?> {
            return arrayOfNulls(size)
        }
    }
}