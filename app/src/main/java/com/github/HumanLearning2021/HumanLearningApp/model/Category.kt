package com.github.HumanLearning2021.HumanLearningApp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Class representing a category to which a CategorizedPicture can belong
 *
 * @property name the name of the category (case-sensitive)
 * @property id uniquely identifies the category
 */
@Parcelize
data class Category(val id: Id, val name: String) : Parcelable
