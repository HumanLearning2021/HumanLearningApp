package com.github.HumanLearning2021.HumanLearningApp

import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.HumanLearning2021.HumanLearningApp.view.fragments.DatasetListRecyclerViewAdapter
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class DatasetListFragmentTest {

    @get:Rule
    val activityScenarioRule: ActivityScenarioRule<AddPictureActivity> = ActivityScenarioRule(
        Intent(
            ApplicationProvider.getApplicationContext(),
            DataOverviewActivity::class.java
        )
    )

    @Before
    fun setUp() {
        Intents.init()
        val delayBeforeTestStart: Long = 5000
        TestUtils.waitFor(delayBeforeTestStart)
    }

    @After
    fun cleanUp() {
        Intents.release()
        activityScenarioRule.scenario.close()
    }

    @Test
    fun test_isListFragmentVisible_onAppLaunch() {
        onView(withId(R.id.dataOverview_fragment)).check(matches(isDisplayed()))

    }

    @Test
    fun listItemInFragmentAreClickable() {
        onView(withId(R.id.dataOverview_fragment))
            .perform(actionOnItemAtPosition<DatasetListRecyclerViewAdapter.ViewHolder>(0, click()))

        // Confirm
        Intents.intended(IntentMatchers.hasComponent(DataCreationActivity::class.java.name))
    }

    @Test
    fun fragmentHasChildrenViews() {
        onView(withId(R.id.dataOverview_fragment)).check(
            matches(
                ViewMatchers.hasChildCount(1)
            )
        )

    }


}