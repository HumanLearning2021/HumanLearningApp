package com.github.HumanLearning2021.HumanLearningApp.view

import android.content.Intent
import android.content.res.Resources
import android.view.KeyEvent
import androidx.navigation.fragment.findNavController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.TestUtils
import com.github.HumanLearning2021.HumanLearningApp.hilt.DatabaseManagementModule
import com.github.HumanLearning2021.HumanLearningApp.hilt.Demo2Database
import com.github.HumanLearning2021.HumanLearningApp.hilt.DemoDatabase
import com.github.HumanLearning2021.HumanLearningApp.hilt.ScratchDatabase
import com.github.HumanLearning2021.HumanLearningApp.model.*
import com.github.HumanLearning2021.HumanLearningApp.view.dataset_list_fragment.DatasetListRecyclerViewAdapter
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.junit.*
import org.junit.runner.RunWith
import javax.inject.Inject

@UninstallModules(DatabaseManagementModule::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class SearchTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)


    @get:Rule
    val activityScenarioRule: ActivityScenarioRule<MainActivity> = ActivityScenarioRule(
        Intent(
            ApplicationProvider.getApplicationContext(),
            MainActivity::class.java
        )
    )

    @BindValue
    @Demo2Database
    val dbManagement: DatabaseManagement = DefaultDatabaseManagement(DummyDatabaseService())

    private val dummyDatasets = runBlocking { dbManagement.getDatasets() }

    @Before
    fun setup() {
        hiltRule.inject()
        Intents.init()
    }

    @After
    fun cleanUp() {
        Intents.release()
        activityScenarioRule.scenario.close()
    }

    @Test
    fun datasetsOverviewInputEmptyStringYieldsAllResults() {
        navigateToDatasetsOverview()
        onView(withId(R.id.action_search)).perform(click(), typeText("    "))
        onView(withId(R.id.DatasetList_list)).check(
            ViewAssertions.matches(
                ViewMatchers.hasChildCount(dummyDatasets.size)
            )
        )
    }

    @Test
    fun datasetsOverviewSearchByKeyWordYieldsCorrectResult() {
        navigateToDatasetsOverview()
        onView(withId(R.id.action_search)).perform(click(), typeText("kitchen"))
        onView(withId(R.id.DatasetList_list)).check(
            ViewAssertions.matches(
                ViewMatchers.hasChildCount(1)
            )
        )
    }

    @Test
    fun datasetsOverviewSearchNotFoundYieldsNoResult() {
        navigateToDatasetsOverview()
        onView(withId(R.id.action_search)).perform(click(), typeText("hi"))
        onView(withId(R.id.DatasetList_list)).check(
            ViewAssertions.matches(
                ViewMatchers.hasChildCount(0)
            )
        )
    }

    @Test
    fun datasetsOverviewCanClickOnSubsetOfDatasetsMatchingSearch() {
        navigateToDatasetsOverview()
        onView(withId(R.id.action_search)).perform(click(), typeText("kitchen"))

        onView(withId(R.id.DatasetList_list))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<DatasetListRecyclerViewAdapter.ListItemViewHolder>(
                    0,
                    click()
                )
            )
        assertCurrentFragmentIsCorrect(R.id.displayDatasetFragment)
    }

    @Test
    @Ignore // haven't found a way to clear the text
    fun datasetsOverviewTypeTextAndThenClearYieldsAllResults() {
        navigateToDatasetsOverview()
        onView(withId(R.id.action_search)).perform(click(), typeText("kitchen"), clearText())
        onView(withId(R.id.DatasetList_list)).check(
            ViewAssertions.matches(
                ViewMatchers.hasChildCount(dummyDatasets.size)
            )
        )
    }

    @Test
    fun learningDatasetSelectionInputEmptyStringYieldsAllResults() {
        navigateToLearningDatasetSelection()
        onView(withId(R.id.action_search)).perform(click(), typeText("    "))
        onView(withId(R.id.DatasetList_list)).check(
            ViewAssertions.matches(
                ViewMatchers.hasChildCount(dummyDatasets.size)
            )
        )
    }

    @Test
    fun learningDatasetSelectionSearchByKeyWordYieldsCorrectResult() {
        navigateToLearningDatasetSelection()
        onView(withId(R.id.action_search)).perform(click(), typeText("kitchen"))
        onView(withId(R.id.DatasetList_list)).check(
            ViewAssertions.matches(
                ViewMatchers.hasChildCount(1)
            )
        )
    }

    @Test
    fun learningDatasetSelectionSearchNotFoundYieldsNoResult() {
        navigateToLearningDatasetSelection()
        onView(withId(R.id.action_search)).perform(click(), typeText("hi"))
        onView(withId(R.id.DatasetList_list)).check(
            ViewAssertions.matches(
                ViewMatchers.hasChildCount(0)
            )
        )
    }

    @Test
    fun learningDatasetSelectionCanClickOnSubsetOfDatasetsMatchingSearch() {
        navigateToLearningDatasetSelection()
        onView(withId(R.id.action_search)).perform(click(), typeText("kitchen"))

        onView(withId(R.id.DatasetList_list))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<DatasetListRecyclerViewAdapter.ListItemViewHolder>(
                    0,
                    click()
                )
            )
        assertCurrentFragmentIsCorrect(R.id.learningSettingsFragment)
    }

    @Test
    @Ignore // haven't found a way to clear the text
    fun learningDatasetSelectionTypeTextAndThenClearYieldsAllResults() {
        navigateToLearningDatasetSelection()
        onView(withId(R.id.action_search)).perform(click(), typeText("kitchen"), clearText())
        onView(withId(R.id.DatasetList_list)).check(
            ViewAssertions.matches(
                ViewMatchers.hasChildCount(dummyDatasets.size)
            )
        )
    }


    private fun navigateToDatasetsOverview() {
        onView(withId(R.id.datasetsOverviewFragment)).perform(click())
    }

    private fun navigateToLearningDatasetSelection() {
        onView(withId(R.id.learningDatasetSelectionFragment)).perform(click())
    }

    private fun assertCurrentFragmentIsCorrect(expected: Int) {
        activityScenarioRule.scenario.onActivity {
            var currentFragmentContainer =
                it.supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container)
            val currentFragment = currentFragmentContainer?.findNavController()?.currentDestination
            ViewMatchers.assertThat(currentFragment?.id, CoreMatchers.equalTo(expected))
        }
    }
}