package com.github.HumanLearning2021.HumanLearningApp.view

import android.content.Intent
import androidx.navigation.fragment.findNavController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.hilt.DatabaseManagementModule
import com.github.HumanLearning2021.HumanLearningApp.hilt.Demo2Database
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.DefaultDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.DummyDatabaseService
import com.github.HumanLearning2021.HumanLearningApp.view.dataset_list_fragment.DatasetListRecyclerViewAdapter
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.junit.*
import org.junit.runner.RunWith

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
        inputEmptyStringYieldsAllResults()
    }

    @Test
    fun learningDatasetSelectionInputEmptyStringYieldsAllResults() {
        navigateToLearningDatasetSelection()
        inputEmptyStringYieldsAllResults()
    }

    @Test
    fun datasetsOverviewSearchByKeyWordYieldsCorrectResult() {
        navigateToDatasetsOverview()
        searchByKeyWordYieldsCorrectResult()
    }


    @Test
    fun learningDatasetSelectionSearchByKeyWordYieldsCorrectResult() {
        navigateToLearningDatasetSelection()
        searchByKeyWordYieldsCorrectResult()
    }

    @Test
    fun datasetsOverviewSearchNotFoundYieldsNoResult() {
        navigateToDatasetsOverview()
        searchNotFoundYieldsNoResult()
    }

    @Test
    fun learningDatasetSelectionSearchNotFoundYieldsNoResult() {
        navigateToLearningDatasetSelection()
        searchNotFoundYieldsNoResult()
    }


    @Test
    fun datasetsOverviewCanClickOnSubsetOfDatasetsMatchingSearch() {
        navigateToDatasetsOverview()
        canClickOnSubsetOfDatasetsMatchingSearch()
        assertCurrentFragmentIsCorrect(R.id.displayDatasetFragment)
    }

    @Test
    fun learningDatasetSelectionCanClickOnSubsetOfDatasetsMatchingSearch() {
        navigateToLearningDatasetSelection()
        canClickOnSubsetOfDatasetsMatchingSearch()
        assertCurrentFragmentIsCorrect(R.id.learningSettingsFragment)
    }

    @Test
    fun datasetsOverviewEmptySpacePrefixHasNoInfluence(){
        navigateToDatasetsOverview()
        emptySpacePrefixHasNoInfluence()
    }

    @Test
    fun learningDatasetSelectionEmptySpacePrefixHasNoInfluence(){
        navigateToLearningDatasetSelection()
        emptySpacePrefixHasNoInfluence()
    }

    @Test
    @Ignore("haven't found a way to clear the text")
    fun datasetsOverviewTypeTextAndThenClearYieldsAllResults() {
        navigateToDatasetsOverview()
        typeTextAndThenClearYieldsAllResults()
    }

    @Test
    @Ignore // haven't found a way to clear the text
    fun learningDatasetSelectionTypeTextAndThenClearYieldsAllResults() {
        navigateToLearningDatasetSelection()
        typeTextAndThenClearYieldsAllResults()
    }

    private fun navigateToDatasetsOverview() {
        onView(withId(R.id.datasetsOverviewFragment)).perform(click())
    }

    private fun navigateToLearningDatasetSelection() {
        onView(withId(R.id.learningDatasetSelectionFragment)).perform(click())
    }


    private fun inputEmptyStringYieldsAllResults() {
        onView(withId(R.id.action_search)).perform(click(), typeText(""))
        onView(withId(R.id.DatasetList_list)).check(
            ViewAssertions.matches(
                ViewMatchers.hasChildCount(dummyDatasets.size)
            )
        )
    }

    private fun emptySpacePrefixHasNoInfluence(){
        onView(withId(R.id.action_search)).perform(click(), typeText("          "))
        onView(withId(R.id.DatasetList_list)).check(
            ViewAssertions.matches(
                ViewMatchers.hasChildCount(dummyDatasets.size)
            )
        )
    }

    private fun typeTextAndThenClearYieldsAllResults() {
        onView(withId(R.id.action_search)).perform(click(), typeText("kitchen"), clearText())
        onView(withId(R.id.DatasetList_list)).check(
            ViewAssertions.matches(
                ViewMatchers.hasChildCount(dummyDatasets.size)
            )
        )
    }

    private fun canClickOnSubsetOfDatasetsMatchingSearch() {
        onView(withId(R.id.action_search)).perform(click(), typeText("kitchen"))
        onView(withId(R.id.DatasetList_list))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<DatasetListRecyclerViewAdapter.ListItemViewHolder>(
                    0,
                    click()
                )
            )
    }

    private fun searchNotFoundYieldsNoResult() {
        onView(withId(R.id.action_search)).perform(click(), typeText("asdfghjkledfvboijhedfvbgbzuhikmolwsxd"))
        onView(withId(R.id.DatasetList_list)).check(
            ViewAssertions.matches(
                ViewMatchers.hasChildCount(0)
            )
        )
    }

    private fun searchByKeyWordYieldsCorrectResult() {
        onView(withId(R.id.action_search)).perform(click(), typeText("kitchen"))
        onView(withId(R.id.DatasetList_list)).check(
            ViewAssertions.matches(
                ViewMatchers.hasChildCount(1)
            )
        )
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