package com.github.HumanLearning2021.HumanLearningApp.model

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.HumanLearning2021.HumanLearningApp.hilt.DemoDatabase
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasItem
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

abstract class DemoDatabaseServiceTest {
    protected abstract val db: DatabaseService

    @Test
    fun test_getCategories() = runBlocking {
        val cats = db.getCategories()
        assertThat(cats, hasItem(hasName("Pomme")))
    }

    @Test
    fun test_getCategory() = runBlocking {
        val cat = db.getCategory("LbaIwsl1kizvTod4q1TG")
        assertThat(cat, hasName("Pomme"))
    }

    @Test
    @Suppress("DEPRECATION")
    fun test_getPicture() = runBlocking {
        val appleCategory = db.getCategories().find { it.name == "Pomme" }
        requireNotNull(appleCategory, { "category of apples no found in demo dataset" })
        val pic = db.getPicture(appleCategory)
        assertThat(pic, hasCategory(equalTo(appleCategory)))
    }
}

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class FirestoreDemoDatabaseServiceTest : DemoDatabaseServiceTest() {
    @Inject
    @DemoDatabase
    override lateinit var db: DatabaseService

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Before
    fun setUpDb() {
        hiltRule.inject()
    }
}