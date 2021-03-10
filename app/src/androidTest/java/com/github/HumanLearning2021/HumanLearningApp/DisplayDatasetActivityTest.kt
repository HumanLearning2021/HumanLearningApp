package com.github.HumanLearning2021.HumanLearningApp

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.HumanLearning2021.HumanLearningApp.DisplayDatasetActivity.DisplayDatasetActivity
import com.github.HumanLearning2021.HumanLearningApp.DisplayDatasetActivity.DisplayImageActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class DisplayDatasetActivityTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(DisplayDatasetActivity::class.java)

    @Test fun DatasetGrisIsDisplayed() {
        onView(withId(R.id.display_dataset_imagesGridView)).check(matches(isDisplayed()))
    }

}