package com.github.HumanLearning2021.HumanLearningApp.model

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Assume.assumeThat
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ConcurrentDeleteDatabaseManagementTest {
    lateinit var db: DatabaseManagement
    lateinit var deletedDataset: Dataset
    lateinit var category: Category

    @Before
    fun setUp() {
        db = ConcurrentDeleteDatabaseManagement(DefaultDatabaseManagement(DummyDatabaseService()))
        runBlocking {
            deletedDataset = db.getDatasets().first()
            db.deleteDataset(deletedDataset.id)
            category = db.getCategories().first()
        }
    }

    @Test
    fun addCategoryToDataset() {
        val dataset = runBlocking {
            db.addCategoryToDataset(deletedDataset, category)
        }
        assertThat(dataset.categories, hasItem(category))
    }

    @Test
    fun removeCategoryFromDataset() {
        val dataset = runBlocking {
            db.removeCategoryFromDataset(deletedDataset, category)
        }
        assertThat(dataset.categories, not(hasItem(category)))
    }

    @Test
    fun editDatasetName() {
        val newName = "very weird new name"
        assumeThat(deletedDataset.name, not(newName))
        val dataset = runBlocking {
            db.editDatasetName(deletedDataset, newName)
        }
        assertThat(dataset.name, equalTo(newName))
    }
}