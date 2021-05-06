package com.github.HumanLearning2021.HumanLearningApp.firestore

/**
 * A snapshot of a document in the firestore database.
 */
interface FirestoreDocument {
    /**
     * Path of the document within the firestore database
     */
    val path: String
}