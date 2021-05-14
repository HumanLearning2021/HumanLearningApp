package com.github.HumanLearning2021.HumanLearningApp.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Representation of a categorized picture
 * @property id unique identifier of the picture
 * @property category to which the picture is assigned
 * @property picture location of the drawable
 */
@Parcelize
data class CategorizedPicture(
    val id: Id, val category: Category, val picture: Uri
) : Parcelable