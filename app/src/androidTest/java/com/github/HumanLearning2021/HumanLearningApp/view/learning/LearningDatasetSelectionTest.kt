package com.github.HumanLearning2021.HumanLearningApp.view.learning

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtraWithKey
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.TestUtils
import com.github.HumanLearning2021.HumanLearningApp.view.dataset_list_fragment.DatasetListRecyclerViewAdapter
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.CoreMatchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class LearningDatasetSelectionTest {
    @get:Rule(order=1)
    val testRule = ActivityScenarioRule(LearningDatasetSelectionActivity::class.java)
    @get:Rule(order=0)
    val hiltRule = HiltAndroidRule(this)


    @Before
    fun before(){
        Intents.init()
        TestUtils.waitFor(100)
    }
    @After
    fun after(){
        Intents.release()
    }

    @Test
    fun allViewsAreDisplayed(){
        assertDisplayed(R.id.LearningDatasetSelection_dataset_list)
    }

    @Test
    fun clickingADatasetLaunchesLearningSettings(){
        onView(ViewMatchers.withId(R.id.DatasetList_list))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<DatasetListRecyclerViewAdapter.ListItemViewHolder>(
                    0,
                    ViewActions.click()
                )
            )

        intended(CoreMatchers.allOf(
            hasComponent(LearningSettingsActivity::class.java.name),
            hasExtraWithKey(LearningDatasetSelectionActivity.EXTRA_SELECTED_DATASET)
        ))
        // TODO test that the extra has type Dataset (don't know how to do it yet)
    }
}
