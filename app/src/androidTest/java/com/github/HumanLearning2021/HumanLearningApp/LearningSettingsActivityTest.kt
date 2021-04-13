package com.github.HumanLearning2021.HumanLearningApp

import android.content.Intent
import android.view.View
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.HumanLearning2021.HumanLearningApp.model.DummyDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.DummyDatabaseService
import com.github.HumanLearning2021.HumanLearningApp.model.DummyDataset
import com.github.HumanLearning2021.HumanLearningApp.view.LearningActivity
import com.github.HumanLearning2021.HumanLearningApp.view.LearningDatasetSelectionActivity
import com.github.HumanLearning2021.HumanLearningApp.view.LearningSettingsActivity
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn
import junit.framework.AssertionFailedError
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.longClickOn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.*
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class LearningSettingsActivityTest {

    @get:Rule
    val activityScenarioRule: ActivityScenarioRule<LearningSettingsActivity> = ActivityScenarioRule(
        Intent(
            ApplicationProvider.getApplicationContext(),
            LearningSettingsActivity::class.java
        ).putExtra(
            LearningDatasetSelectionActivity.EXTRA_SELECTED_DATASET,
            runBlocking {
                DummyDatabaseManagement(DummyDatabaseService()).getDatasets().first()
            }
        )
    )

    @get:Rule
    val hiltRule = HiltAndroidRule(this)


    @Before
    fun setUp() {
        Intents.init()
        // By waiting before the test starts, it allows time for the app to startup to prevent the
        // following error to appear on cirrus:
        // `Waited for the root of the view hierarchy to have window focus and not request layout for 10 seconds.`
        // This solution is not ideal because it slows down the tests, and it might not work
        // every time. But there isn't a better solution that I (Niels Lachat) know of.
        val delayBeforeTestStart: Long = 2000
        TestUtils.waitFor(delayBeforeTestStart)
    }


    @After
    fun cleanUp() {
        Intents.release()
    }

    @Test
    fun bothButtonsAndTVAreDisplayed() {
        assertDisplayed(R.id.learningSettings_btChoosePresentation)
        assertDisplayed(R.id.learningSettings_btChooseRepresentation)
        assertDisplayed(R.id.learningSettings_tvMode)
    }

    private fun pressingButtonLaunchesLearningActivity(btnId: Int) {
        clickOn(btnId)
        intended(allOf(
            hasComponent(LearningActivity::class.java.name),
            IntentMatchers.hasExtraWithKey(LearningDatasetSelectionActivity.EXTRA_SELECTED_DATASET),
            IntentMatchers.hasExtraWithKey(LearningSettingsActivity.EXTRA_LEARNING_MODE)
        ))
    }

    @Test
    fun pressingPresentationButtonLaunchesLearningActivity() {
        pressingButtonLaunchesLearningActivity(R.id.learningSettings_btChoosePresentation)
    }

    @Test
    fun pressingRepresentationButtonLaunchesLearningActivity() {
        pressingButtonLaunchesLearningActivity(R.id.learningSettings_btChooseRepresentation)
    }

    @Test
    fun learningModeTooltipsAreCorrect() {
        val res = InstrumentationRegistry.getInstrumentation().targetContext.resources
        onView(withId(R.id.learningSettings_btChoosePresentation))
            .check(HasTooltipText(res.getString(R.string.learning_settings_tooltip_presentation)))
        onView(withId(R.id.learningSettings_btChooseRepresentation))
            .check(HasTooltipText(res.getString(R.string.learning_settings_tooltip_representation)))
    }

    inner class HasTooltipText(private val text: String) : ViewAssertion {
        override fun check(view: View?, noViewFoundException: NoMatchingViewException?) {
            if (view != null && noViewFoundException == null) {
                if (view.tooltipText.toString() != text) {
                    throw AssertionFailedError("The tooltip text was different than expected " +
                            "was \"${view.tooltipText}\", expected \"$text\"")
                }
            } else {
                throw AssertionFailedError("The view was not found")
            }
        }
    }
}
