package com.github.HumanLearning2021.HumanLearningApp

import com.github.HumanLearning2021.HumanLearningApp.datamodel.Category
import com.github.HumanLearning2021.HumanLearningApp.datamodel.Picture
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import junit.framework.TestCase

class DatabaseTest : TestCase() {
    fun testLoadApple() {
        Firebase.firestore.document("/databases/demo/pictures/minecraft_apple").get()
            .continueWithTask {
                val img = it.result?.toObject<Picture>()
                assertNotNull(img?.category)
                img!!.category!!.get()
            }.continueWith {
            val category = it.result?.toObject<Category>()
            assertNotNull(category)
        }.also {
            while (!it.isComplete) {
            }
        }
    }
}