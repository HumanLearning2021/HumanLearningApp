package com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing

import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.TestUtils
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.After
import org.junit.Before


@RunWith(AndroidJUnit4::class)

@HiltAndroidTest
class DatasetsOverviewActivityTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)
    @get:Rule
    val activityScenarioRule: ActivityScenarioRule<TakePictureActivity> = ActivityScenarioRule(
        Intent(
            ApplicationProvider.getApplicationContext(),
            DatasetsOverviewActivity::class.java
        )
    )

    @Before
    fun setUp() {
        Intents.init()
        val delayBeforeTestStart: Long = 3000
        TestUtils.waitFor(delayBeforeTestStart)
    }

    @After
    fun cleanUp() {
        Intents.release()
        activityScenarioRule.scenario.close()
    }


    @Test
    fun fragmentIsDisplayedWhenActivityIsLaunched() {
        assertDisplayed(R.id.dataOverview_fragment)
        assertDisplayed(R.id.dataOverviewButton)
    }

    @Test
    fun rightActivityIsStartedAfterCreateButton() {
        onView(withId(R.id.dataOverviewButton)).perform(click())
        intended(hasComponent(CategoriesEditingActivity::class.java.name))
    }


}