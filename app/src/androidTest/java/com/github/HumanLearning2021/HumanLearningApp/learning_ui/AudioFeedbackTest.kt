package com.github.HumanLearning2021.HumanLearningApp.learning_ui

import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.github.HumanLearning2021.HumanLearningApp.model.DummyDataset
import com.github.HumanLearning2021.HumanLearningApp.view.*
import com.github.HumanLearning2021.HumanLearningApp.view.LearningActivity
import com.github.HumanLearning2021.HumanLearningApp.view.LearningAudioFeedback
import com.github.HumanLearning2021.HumanLearningApp.view.LearningMode
import com.github.HumanLearning2021.HumanLearningApp.view.LearningSettingsActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.Assert.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class AudioFeedbackTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val activityScenarioRule: ActivityScenarioRule<LearningActivity> = ActivityScenarioRule(
        Intent(
            ApplicationProvider.getApplicationContext(),
            LearningActivity::class.java
        )
            .putExtra(LearningSettingsActivity.EXTRA_LEARNING_MODE, LearningMode.PRESENTATION)
            .putExtra(LearningDatasetSelectionActivity.EXTRA_SELECTED_DATASET, DummyDataset("id", "name", emptySet()))
    )

    fun makeLearningAudioFeedback(): LearningAudioFeedback {
        return LearningAudioFeedback(getInstrumentation().targetContext)
    }

    fun assertBothMPsNotPlaying(af: LearningAudioFeedback) {
        assertThat(af.__testing_getCorrectMP().isPlaying, `is`(false))
        assertThat(af.__testing_getIncorrectMP().isPlaying, `is`(false))
    }

    @Test
    fun initPutsMediaPlayersInCorrectState() {
        val af = makeLearningAudioFeedback()
        af.initMediaPlayers()
        assertThat(af.__testing_getCorrectMP(), notNullValue())
        assertThat(af.__testing_getIncorrectMP(), notNullValue())
        // the media players should be prepared but not playing yet
        assertBothMPsNotPlaying(af)
        af.releaseMediaPlayers()
    }

    @Test
    fun canStartAndStopMediaPlayersRapidly() {
        val af = makeLearningAudioFeedback()
        af.initMediaPlayers()
        af.startCorrectFeedback()
        assertThat(af.__testing_getCorrectMP().isPlaying, `is`(true))
        af.stopAndPrepareMediaPlayers()
        assertBothMPsNotPlaying(af)
        af.startIncorrectFeedback()
        assertThat(af.__testing_getIncorrectMP().isPlaying, `is`(true))
        af.releaseMediaPlayers()
    }
}
