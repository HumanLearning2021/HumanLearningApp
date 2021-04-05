package com.github.HumanLearning2021.HumanLearningApp

import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.HumanLearning2021.HumanLearningApp.view.LearningActivity
import com.github.HumanLearning2021.HumanLearningApp.view.LearningSettingsActivity
import com.schibsted.spain.barista.assertion.BaristaClickableAssertions.assertNotClickable
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.longClickOn
import org.junit.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LearningSettingsActivityTest {

    @get:Rule
    val testRule = ActivityScenarioRule(LearningSettingsActivity::class.java)


    @Before
    fun setUp() {
        Intents.init()
        // By waiting before the test starts, it allows time for the app to startup to prevent the
        // following error to appear on cirrus:
        // `Waited for the root of the view hierarchy to have window focus and not request layout for 10 seconds.`
        // This solution is not ideal because it slows down the tests, and it might not work
        // every time. But there isn't a better solution that I (Niels Lachat) know of.
        val delayBeforeTestStart: Long = 3000
        TestUtils.waitFor(delayBeforeTestStart)
    }


    @After
    fun cleanUp() {
        Intents.release()
        testRule.scenario.close()
    }

    @Test
    fun bothButtonsAndTVAreDisplayed() {
        assertDisplayed(R.id.learningSettings_btChoosePresentation)
        assertDisplayed(R.id.learningSettings_btChooseRepresentation)
        assertDisplayed(R.id.learningSettings_tvMode)

    }

    @Test
    fun pressingPresentationButtonLaunchesLearningActivity() {
        clickOn(R.id.learningSettings_btChoosePresentation)
        intended(hasComponent(LearningActivity::class.java.name))
    }

    @Test
    fun pressingrepresentationButtonLaunchesLearningActivity() {
        clickOn(R.id.learningSettings_btChoosePresentation)
        intended(hasComponent(LearningActivity::class.java.name))
    }

    @Test
    fun longClickOnPresentationButtonShowsTooltip() {
        longClickOn(R.id.learningSettings_btChoosePresentation)

        //assertDisplayed(R.string.learning_settings_tooltip_presentation)

    }

    @Test
    fun presentationModeWorks(){

    }

    @Test
    fun representationModeWorks(){

    }
}
