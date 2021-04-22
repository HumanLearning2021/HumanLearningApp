package com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing

import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.TestUtils
import com.github.HumanLearning2021.HumanLearningApp.TestUtils.waitFor
import com.github.HumanLearning2021.HumanLearningApp.hilt.ScratchDatabase
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.view.MainActivity
import com.github.HumanLearning2021.HumanLearningApp.view.dataset_list_fragment.DatasetListRecyclerViewAdapter
import com.github.HumanLearning2021.HumanLearningApp.view.learning.LearningDatasetSelectionActivity
import com.github.HumanLearning2021.HumanLearningApp.view.learning.LearningSettingsActivity
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.junit.*
import org.junit.runner.RunWith
import javax.inject.Inject


@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class DatasetsOverviewActivityTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)
    @get:Rule
    val activityScenarioRule: ActivityScenarioRule<AddPictureActivity> = ActivityScenarioRule(
        Intent(
            ApplicationProvider.getApplicationContext(),
            DatasetsOverviewActivity::class.java
        )
    )

    @Before
    fun setUp() {
        Intents.init()
        val delayBeforeTestStart: Long = 1000
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
    //Waiting PR #103
    /*
    @Test
    fun rightActivityIsStartedAfterCreateButtonIsClicked() {
        onView(withId(R.id.datasetsOverviewButton)).perform(click())
        intended(hasComponent(CategoriesEditingActivity::class.java.name))
        onView(withId(R.id.button_submit_list)).perform(click())
    }

    @Test
    fun whenClickOnDatasetDisplayDatasetActivity() {
        waitFor(10000)
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
    */

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
