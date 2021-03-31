package com.github.HumanLearning2021.HumanLearningApp.model

import com.github.HumanLearning2021.HumanLearningApp.firestore.FirestoreDatabaseService
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasItem
import org.junit.Assert.assertThat


abstract class DemoDatabaseServiceTest : TestCase() {
    private lateinit var db: DatabaseService

    override fun setUp() {
        db = setUpDatabaseService()
    }

    protected abstract fun setUpDatabaseService(): DatabaseService

    fun test_getCategories() = runBlocking {
        val cats = db.getCategories()
        assertThat(cats, hasItem(hasName("Pomme")))
    }

    fun test_getCategory() = runBlocking {
        val cat = db.getCategory("Pomme")
        assertThat(cat, hasName("Pomme"))
    }

    //TODO()
    fun test_getDataset() = runBlocking {
        assert(true)
    }

    fun test_getPicture() = runBlocking {
        val appleCategory = db.getCategories().find { it.name == "Pomme" }
        requireNotNull(appleCategory, { "category of apples no found in demo dataset" })
        val pic = db.getPicture(appleCategory)
        assertThat(pic, hasCategory(equalTo(appleCategory)))
    }
}

class FirestoreDemoDatabaseServiceTest : DemoDatabaseServiceTest() {
    override fun setUpDatabaseService() = FirestoreDatabaseService("demo")
}