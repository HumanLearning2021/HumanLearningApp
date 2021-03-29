package com.github.HumanLearning2021.HumanLearningApp.model

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parcelize


/**
 * Class representing a dummy implementation of the category interface
 *
 * @param name the name of the category (case-sensitive)
 */
@Parcelize
data class DummyCategory(override val name: String,
                         override val representativePicture: CategorizedPicture?
): Category {
    override fun equals(other: Any?): Boolean {
        return (other is DummyCategory && other.name == this.name)
    }
}