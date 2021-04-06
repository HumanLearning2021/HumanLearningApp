package com.github.HumanLearning2021.HumanLearningApp.model

import kotlinx.parcelize.Parcelize

/**
 * Class representing a dummy implementation of the category interface
 *
 * @param name the name of the category (case-sensitive)
 * @param id uniquely identifies the category
 * @param representativePicture a categorized picture, can be null
 */
@Parcelize
data class DummyCategory(override val id: String, override val name: String): Category {
    override fun equals(other: Any?): Boolean {
        return (other is DummyCategory) && other.id == this.id
    }
}