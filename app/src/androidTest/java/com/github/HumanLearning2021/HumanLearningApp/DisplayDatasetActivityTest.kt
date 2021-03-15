package com.github.HumanLearning2021.HumanLearningApp

import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.HumanLearning2021.HumanLearningApp.DisplayDatasetActivity.DisplayDatasetActivity
import com.github.HumanLearning2021.HumanLearningApp.DisplayDatasetActivity.DisplayImageActivity
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.anything
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class DisplayDatasetActivityTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(DisplayDatasetActivity::class.java)

    @get:Rule
    var activityRuleIntent = IntentsTestRule(DisplayDatasetActivity::class.java)

    @Test fun DatasetGridIsDisplayed() {
        onView(withId(R.id.display_dataset_imagesGridView)).check(matches(isDisplayed()))
    }

    @Test fun WhenClickOnImageDisplayImageActivity(){
        onData(anything())
                .inAdapterView(withId(R.id.display_dataset_imagesGridView))
                .atPosition(0)
                .perform(click())

        var firstImage = DatasetImageModel("chat", R.drawable.chat1)

        intended(allOf(hasComponent(DisplayImageActivity::class.java.name)))
    }

}