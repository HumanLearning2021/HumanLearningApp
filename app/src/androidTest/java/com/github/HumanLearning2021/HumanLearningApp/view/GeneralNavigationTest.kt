package com.github.HumanLearning2021.HumanLearningApp.view

import android.content.Intent
import androidx.navigation.fragment.findNavController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.DrawerMatchers.isOpen
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.hilt.DatabaseManagementModule
import com.github.HumanLearning2021.HumanLearningApp.hilt.Demo2Database
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.DummyDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.DummyDatabaseService
import com.github.HumanLearning2021.HumanLearningApp.view.dataset_list_fragment.DatasetListRecyclerViewAdapter
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.hamcrest.CoreMatchers.equalTo
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@UninstallModules(DatabaseManagementModule::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class GeneralNavigationTest {

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
    val dbManagement: DatabaseManagement = DummyDatabaseManagement(DummyDatabaseService())

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


    @Test // this one used to cause trouble
    fun navigateToDisplayDatasetAndThenNavigateUpGoesToDatasetsOverview() {
        navigateToDisplayDataset()
        assertCurrentFragmentIsCorrect(R.id.displayDatasetFragment)
        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())
        assertCurrentFragmentIsCorrect(R.id.datasetsOverviewFragment)
    }

    @Test
    fun createDatasetAndNavigateUpGoesToDatasetsOverview() {
        onView(withId(R.id.datasetsOverviewFragment)).perform(click())
        onView(withId(R.id.createDatasetButton)).perform(click())
        assertCurrentFragmentIsCorrect(R.id.categoriesEditingFragment)
        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())
        assertCurrentFragmentIsCorrect(R.id.datasetsOverviewFragment)
    }

    @Test
    fun openingNavigationAndClickingOnLoginWorks() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.drawer_layout)).check(matches(isOpen()));
        onView(withId(R.id.googleSignInWidget)).perform(click())
        assertCurrentFragmentIsCorrect(R.id.googleSignInWidget)
    }

    @Test
    fun bottomNavigationCanNavigateToLearning() {
        onView(withId(R.id.learningDatasetSelectionFragment)).perform(click())
        assertCurrentFragmentIsCorrect(R.id.learningDatasetSelectionFragment)
    }

    @Test
    fun bottomNavigationCanNavigateToDatasetEditing() {
        onView(withId(R.id.learningDatasetSelectionFragment)).perform(click())
        assertCurrentFragmentIsCorrect(R.id.learningDatasetSelectionFragment)
    }

    @Test
    fun buttonOnHomeToLearningWorks() {
        onView(withId(R.id.startLearningButton)).perform(click())
        assertCurrentFragmentIsCorrect(R.id.learningDatasetSelectionFragment)
    }

    private fun navigateToDisplayDataset() {
        onView(withId(R.id.datasetsOverviewFragment)).perform(click())
        Espresso.onView(ViewMatchers.withId(R.id.DatasetList_list))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<DatasetListRecyclerViewAdapter.ListItemViewHolder>(
                    0,
                    ViewActions.click()
                )
            )
    }

    private fun assertCurrentFragmentIsCorrect(expected: Int) {
        activityScenarioRule.scenario.onActivity {
            var currentFragmentContainer =
                it.supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container)
            val currentFragment = currentFragmentContainer?.findNavController()?.currentDestination
            assertThat(currentFragment?.id, equalTo(expected))
        }
    }


}