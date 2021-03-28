package com.github.HumanLearning2021.HumanLearningApp.model

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable


/**
 * Class representing a dummy implementation of the category interface
 *
 * @param name the name of the category (case-sensitive)
 */

data class DummyCategory(override val name: String,
                         override val representativePicture: CategorizedPicture?
): Category {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readParcelable(CategorizedPicture::class.java.classLoader)
    ) {
    }

    override fun equals(other: Any?): Boolean {
        return (other is DummyCategory) && other.name == this.name
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeParcelable(representativePicture, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DummyCategory> {
        override fun createFromParcel(parcel: Parcel): DummyCategory {
            return DummyCategory(parcel)
        }

        override fun newArray(size: Int): Array<DummyCategory?> {
            return arrayOfNulls(size)
        }
    }
}