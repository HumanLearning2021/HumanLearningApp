package com.github.HumanLearning2021.HumanLearningApp.model

import java.io.Serializable


/**
 * Class representing a dummy implementation of the category interface
 *
 * @param name the name of the category (case-sensitive)
 */

data class DummyCategory(override val name: String,
                         override val representativePicture: CategorizedPicture?
): Category