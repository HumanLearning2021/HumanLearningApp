package com.github.HumanLearning2021.HumanLearningApp

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)

class DataOverViewActivityTest {

    @get:Rule
    var testRule = ActivityTestRule(DataOverviewActivity::class.java)
    val intentRule = IntentsTestRule(DataOverviewActivity::class.java)


    @Test
    fun fragmentIsDisplayedWhenActivityIsLaunched(){
        assertDisplayed(R.id.dataOverview_fragment)
    }

}