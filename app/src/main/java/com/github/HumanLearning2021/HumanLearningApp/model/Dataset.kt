package com.github.HumanLearning2021.HumanLearningApp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Representation of a Dataset.
 * @property id uniquely identifies the Dataset
 * @property name Human readable name of the Dataset
 * @property categories Set of Category included in the Dataset
 */
@Parcelize
data class Dataset(
    val id: Id,
    val name: String,
    val categories: Set<Category>
) : Parcelable

typealias DatasetId = Id
