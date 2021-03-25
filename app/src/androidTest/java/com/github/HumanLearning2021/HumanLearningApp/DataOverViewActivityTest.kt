package com.github.HumanLearning2021.HumanLearningApp

import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.github.HumanLearning2021.HumanLearningApp.view.DisplayDatasetActivity
import com.github.HumanLearning2021.HumanLearningApp.view.DisplayImageActivity
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before


@RunWith(AndroidJUnit4::class)

class DataOverViewActivityTest {

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
    fun fragmentIsDisplayedWhenActivityIsLaunched(){
        assertDisplayed(R.id.dataOverview_fragment)
        assertDisplayed(R.id.dataOverviewButton)
    }

    @Test
    fun rightActivityIsStartedAfterCreateButton(){
        onView(withId(R.id.dataOverviewButton)).perform(click())
        intended(hasComponent(DataCreationActivity::class.java.name))
    }














}