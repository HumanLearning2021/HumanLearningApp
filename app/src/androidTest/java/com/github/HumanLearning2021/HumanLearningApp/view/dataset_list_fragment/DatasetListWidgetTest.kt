package com.github.HumanLearning2021.HumanLearningApp.view.dataset_list_fragment

import android.content.Context
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.android.architecture.blueprints.todoapp.launchFragmentInHiltContainer
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.hilt.DatabaseManagementModule
import com.github.HumanLearning2021.HumanLearningApp.hilt.Demo2Database
import com.github.HumanLearning2021.HumanLearningApp.hilt.RoomDatabase
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.DefaultDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.DummyDatabaseService
import com.github.HumanLearning2021.HumanLearningApp.room.RoomOfflineDatabase
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@UninstallModules(DatabaseManagementModule::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class DatasetListWidgetTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    @ApplicationContext
    lateinit var context: Context

    @Inject
    @RoomDatabase
    lateinit var room: RoomOfflineDatabase

    @BindValue
    @Demo2Database
    lateinit var dbMgt: DatabaseManagement

    private val dummyDatasets = runBlocking { dbMgt.getDatasets() }

    @Before
    fun setUp() {
        hiltRule.inject()
        dbMgt = DefaultDatabaseManagement(DummyDatabaseService(), "dummy", context, room)
    }

    @Test
    fun listItemInFragmentAreClickable() {
        launchFragmentInHiltContainer<DatasetListWidget> {
            this.selectedDataset.observe(this) {
                // tests that the clicked dataset is in the dummy datasets
                assert(dummyDatasets.find { ds -> ds == it } != null
                ) { "clicked dataset $it was not in $dummyDatasets" }
            }
        }
        onView(withId(R.id.DatasetList_list))
            .perform(
                actionOnItemAtPosition<DatasetListRecyclerViewAdapter.ListItemViewHolder>(
                    0,
                    click()
                )
            )
    }

    @Test
    fun fragmentHasChildrenViews() {
        launchFragmentInHiltContainer<DatasetListWidget>()
        onView(withId(R.id.DatasetList_list)).check(
            matches(
                ViewMatchers.hasChildCount(dummyDatasets.size)
            )
        )
    }

    // TODO test that category representative are correctly displayed on each item
}
