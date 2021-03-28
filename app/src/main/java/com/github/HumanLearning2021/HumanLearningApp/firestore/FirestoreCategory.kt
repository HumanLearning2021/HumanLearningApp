package com.github.HumanLearning2021.HumanLearningApp.firestore

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import com.github.HumanLearning2021.HumanLearningApp.model.CategorizedPicture
import com.github.HumanLearning2021.HumanLearningApp.model.Category

data class FirestoreCategory internal constructor(
    override val path: String,
    override val name: String, override val representativePicture: FirestoreCategorizedPicture?,
) : Category, FirestoreDocument {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readParcelable(FirestoreCategorizedPicture::class.java.classLoader)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(path)
        parcel.writeString(name)
        parcel.writeParcelable(representativePicture, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FirestoreCategory> {
        override fun createFromParcel(parcel: Parcel): FirestoreCategory {
            return FirestoreCategory(parcel)
        }

        override fun newArray(size: Int): Array<FirestoreCategory?> {
            return arrayOfNulls(size)
        }
    }
}