package com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing

import android.content.Intent
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
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.TestUtils.waitFor
import com.github.HumanLearning2021.HumanLearningApp.hilt.DatabaseManagementModule
import com.github.HumanLearning2021.HumanLearningApp.hilt.Demo2Database
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.DummyDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.DummyDatabaseService
import com.github.HumanLearning2021.HumanLearningApp.view.MainActivity
import com.github.HumanLearning2021.HumanLearningApp.view.dataset_list_fragment.DatasetListRecyclerViewAdapter
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.hamcrest.CoreMatchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@UninstallModules(DatabaseManagementModule::class)
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class DatasetsOverviewActivityTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)
    @get:Rule
    val activityScenarioRule: ActivityScenarioRule<DatasetsOverviewActivity> = ActivityScenarioRule(
        Intent(
            ApplicationProvider.getApplicationContext(),
            DatasetsOverviewActivity::class.java
        )
    )

    @BindValue
    @Demo2Database
    val dbMgt: DatabaseManagement = DummyDatabaseManagement(DummyDatabaseService())

    @Before
    fun setUp() {
        Intents.init()
        val delayBeforeTestStart: Long = 1 // increase if needed
        waitFor(delayBeforeTestStart)
    }

    @After
    fun cleanUp() {
        Intents.release()
        activityScenarioRule.scenario.close()
    }


    @Test
    fun fragmentIsDisplayedWhenActivityIsLaunched() {
        assertDisplayed(R.id.datasetsOverview_fragment)
        assertDisplayed(R.id.datasetsOverviewButton)
    }

    @Test
    fun rightActivityIsStartedAfterCreateButtonIsClicked() {
        onView(withId(R.id.datasetsOverviewButton)).perform(click())
        intended(hasComponent(CategoriesEditingActivity::class.java.name))
        onView(withId(R.id.button_submit_list)).perform(click())
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

        intended(CoreMatchers.allOf(
            hasComponent(DisplayDatasetActivity::class.java.name),
            IntentMatchers.hasExtraWithKey("dataset_id")
        ))
    }

    @Test
    fun onBackPressedWorks() {
        Espresso.pressBack()
        intended(
            CoreMatchers.allOf(
                hasComponent(MainActivity::class.java.name),
            )
        )
    }


}
