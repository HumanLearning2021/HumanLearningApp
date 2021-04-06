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
class DummyCategory(override val id: String, override val name: String, override var representativePicture: CategorizedPicture?): Category {

    // Have to override these 2 and not make it a data class because of DummyDatabaseService's initialization:
    // A category depends on a rep pic; and a rep pic depends on a category. Making CategorizedPicture
    // a data class led to an endless loop. If someone has a more elegant solution, would be nice,
    // but otherwise it's only the Dummy implementation

    // This is really ducktaped as I had to ommit representativePicture from both of these, otherwise
    // it caused the endless loop
    override fun equals(other: Any?): Boolean {
        return other is DummyCategory && other.id == id && other.name == name
    }

    override fun hashCode(): Int {
        return 17 + 31*id.hashCode() + 31*name.hashCode()
    }
}