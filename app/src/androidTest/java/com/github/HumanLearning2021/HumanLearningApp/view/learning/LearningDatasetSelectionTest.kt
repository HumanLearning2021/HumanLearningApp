package com.github.HumanLearning2021.HumanLearningApp.view.learning

import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.android.architecture.blueprints.todoapp.launchFragmentInHiltContainer
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.TestUtils
import com.github.HumanLearning2021.HumanLearningApp.hilt.DatabaseManagementModule
import com.github.HumanLearning2021.HumanLearningApp.hilt.Demo2Database
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.DefaultDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.DummyDatabaseService
import com.github.HumanLearning2021.HumanLearningApp.view.dataset_list_fragment.DatasetListRecyclerViewAdapter
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.verify


@UninstallModules(DatabaseManagementModule::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class LearningDatasetSelectionTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @BindValue
    @Demo2Database
    val dbManagement: DatabaseManagement = DefaultDatabaseManagement(DummyDatabaseService())

    private val datasetId = TestUtils.getFirstDataset(dbManagement).id

    private val navController: NavController = Mockito.mock(NavController::class.java)

    @Before
    fun setup() {
        hiltRule.inject()
        launchFragment()
    }

    @Test
    fun allViewsAreDisplayed() {
        assertDisplayed(R.id.LearningDatasetSelection_dataset_list)
    }


    @Test
    fun clickingADatasetLaunchesLearningSettings() {
        onView(ViewMatchers.withId(R.id.DatasetList_list))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<DatasetListRecyclerViewAdapter.ListItemViewHolder>(
                    0,
                    ViewActions.click()
                )
            )
        verify(navController).navigate(
            LearningDatasetSelectionFragmentDirections.actionLearningDatasetSelectionFragmentToLearningSettingsFragment(
                datasetId
            )
        )
    }

    private fun launchFragment() {
        launchFragmentInHiltContainer<LearningDatasetSelectionFragment> {
            Navigation.setViewNavController(requireView(), navController)
        }
    }
}

