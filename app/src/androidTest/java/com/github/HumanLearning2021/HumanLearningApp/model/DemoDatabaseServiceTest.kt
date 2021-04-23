package com.github.HumanLearning2021.HumanLearningApp.model

import com.github.HumanLearning2021.HumanLearningApp.firestore.FirestoreDatabaseService
import com.github.HumanLearning2021.HumanLearningApp.hilt.DemoDatabase
import dagger.hilt.android.testing.HiltAndroidRule
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasItem
import org.junit.Rule
import javax.inject.Inject


abstract class DemoDatabaseServiceTest : TestCase() {
    @Inject
    @DemoDatabase
    lateinit var db: DatabaseService

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    override fun setUp() {
        hiltRule.inject()
    }

    fun test_getCategories() = runBlocking {
        val cats = db.getCategories()
        assertThat(cats, hasItem(hasName("Pomme")))
    }

    fun test_getCategory() = runBlocking {
        val cat = db.getCategory("LbaIwsl1kizvTod4q1TG")
        assertThat(cat, hasName("Pomme"))
    }

    @Suppress("DEPRECATION")
    fun test_getPicture() = runBlocking {
        val appleCategory = db.getCategories().find { it.name == "Pomme" }
        requireNotNull(appleCategory, { "category of apples no found in demo dataset" })
        val pic = db.getPicture(appleCategory)
        assertThat(pic, hasCategory(equalTo(appleCategory)))
    }
}