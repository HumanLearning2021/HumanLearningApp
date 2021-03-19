package com.github.HumanLearning2021.HumanLearningApp.firestore

import com.github.HumanLearning2021.HumanLearningApp.model.hasCategory
import com.github.HumanLearning2021.HumanLearningApp.model.hasName
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasItem
import org.junit.Assert.assertThat


class FirestoreDataSetInterfaceTest : TestCase() {
    lateinit var theInterface: FirestoreDatasetInterface

    override fun setUp() {
        theInterface = FirestoreDatasetInterface("demo")
    }

    fun test_getCategories() = runBlocking {
        val cats = theInterface.getCategories()
        assertThat(cats, hasItem(hasName("Pomme")))
    }

    fun test_getPicture() = runBlocking {
        val appleCategory = theInterface.getCategories().find { it.name == "Pomme" }
        requireNotNull(appleCategory, { "category of apples no found in demo dataset" })
        val pic = theInterface.getPicture(appleCategory)
        assertThat(pic, hasCategory(equalTo(appleCategory)))
    }
}
