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
data class DummyCategory(override val id: Id, override val name: String) : Category