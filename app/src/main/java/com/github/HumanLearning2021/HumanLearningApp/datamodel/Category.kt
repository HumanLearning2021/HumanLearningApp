package com.github.HumanLearning2021.HumanLearningApp.datamodel

/** Plain object for the database entity that represents an abstract category that learners can
 * learn to recognize and classify.
 */
data class Category(
    /** Name of the category in user language. */
    val name: String? = null,
    /** Level 1: presentation, (default) level 2: representation */
    val comWoorLevel: Int = 1,
)