package com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.android.architecture.blueprints.todoapp.launchFragmentInHiltContainer
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.TestUtils.getFirstDataset
import com.github.HumanLearning2021.HumanLearningApp.TestUtils.waitFor
import com.github.HumanLearning2021.HumanLearningApp.hilt.DatabaseManagementModule
import com.github.HumanLearning2021.HumanLearningApp.hilt.Demo2Database
import com.github.HumanLearning2021.HumanLearningApp.model.*
import com.github.HumanLearning2021.HumanLearningApp.view.MainActivity
import com.github.HumanLearning2021.HumanLearningApp.view.dataset_list_fragment.DatasetListRecyclerViewAdapter
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.verify
import java.io.File
import java.util.*


@UninstallModules(DatabaseManagementModule::class)
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class DatasetsOverviewActivityTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @BindValue
    @Demo2Database
    val dbManagement: DatabaseManagement = DummyDatabaseManagement(DummyDatabaseService())

    private val datasetId: String = getFirstDataset(dbManagement).id
    private val navController: NavController = Mockito.mock(NavController::class.java)

    @Before
    fun setup() {
        hiltRule.inject()
        launchFragment()
    }

    @Test
    fun fragmentIsDisplayedWhenActivityIsLaunched() {
        assertDisplayed(R.id.datasetListFragment)
        assertDisplayed(R.id.createDatasetButton)
    }

    @Test
    fun rightActivityIsStartedAfterCreateButtonIsClicked() {
        onView(withId(R.id.createDatasetButton)).perform(click())
        verify(navController).navigate(
            DatasetsOverviewFragmentDirections.actionDatasetsOverviewFragmentToCategoriesEditingFragment(null)
        )
        //onView(withId(R.id.button_submit_list)).perform(click())
    }

    @Test
    fun whenClickOnDatasetDisplayDatasetActivity() {
        onView(withId(R.id.DatasetList_list))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<DatasetListRecyclerViewAdapter.ListItemViewHolder>(
                    0,
                    click()
                )
            )

        verify(navController).navigate(
            DatasetsOverviewFragmentDirections.actionDatasetsOverviewFragmentToDisplayDatasetFragment(
                datasetId
            )
        )
    }

    @Test
    fun backButtonWorks(){
        Espresso.pressBack()
        verify(navController).popBackStack()
    }


    private fun launchFragment() {
        val args = bundleOf("datasetId" to datasetId)
        launchFragmentInHiltContainer<DatasetsOverviewFragment>(args) {
            Navigation.setViewNavController(requireView(), navController)
        }
    }
}
