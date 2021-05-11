package com.github.HumanLearning2021.HumanLearningApp.model

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.HumanLearning2021.HumanLearningApp.hilt.DatabaseManagementModule
import com.github.HumanLearning2021.HumanLearningApp.hilt.Demo2Database
import com.github.HumanLearning2021.HumanLearningApp.hilt.RoomDatabase
import com.github.HumanLearning2021.HumanLearningApp.room.RoomOfflineDatabase
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Assume.assumeThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class ConcurrentDeleteDatabaseManagementTest {
    lateinit var db: DatabaseManagement
    lateinit var deletedDataset: Dataset
    lateinit var category: Category

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    @Demo2Database
    lateinit var demo2DbService: DatabaseService

    @BindValue
    @Demo2Database
    lateinit var demo2DbMgt: DatabaseManagement

    @Inject
    @ApplicationContext
    lateinit var context: Context

    @Inject
    @RoomDatabase
    lateinit var room: RoomOfflineDatabase

    @Before
    fun setUp() {
        hiltRule.inject()
        demo2DbMgt = DatabaseManagementModule.provideDemo2Service(demo2DbService, context, room)
        db = ConcurrentDeleteDatabaseManagement(DefaultDatabaseManagement(DummyDatabaseService(), "dummy", context, room))
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