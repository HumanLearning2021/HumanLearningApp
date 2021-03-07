package com.github.HumanLearning2021.HumanLearningApp.datamodel

import com.google.firebase.firestore.DocumentReference

/** Plain object for the database entity that represents a picture that learners can classify.
 */
data class Picture(
    /** Location of the image in Cloud Storage. */
    val url: String? = null,
    /** The category that the picture depicts. (mandatory) */
    val category: DocumentReference? = null,
)