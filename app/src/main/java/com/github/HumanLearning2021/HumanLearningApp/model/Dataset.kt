package com.github.HumanLearning2021.HumanLearningApp.model

import android.os.Parcelable


/**
 * Interface describing a Dataset.
 * @property id should be used to uniquely identify the Dataset
 * @property name Human readable name of the Dataset
 * @property categories Set of Category included in the Dataset
 */
interface Dataset : Parcelable {
    val id: Id
    val name: String
    val categories: Set<Category>
}