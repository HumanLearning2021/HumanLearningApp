package com.github.HumanLearning2021.HumanLearningApp.view.learning

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.example.android.architecture.blueprints.todoapp.launchFragmentInHiltContainer
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.TestUtils.getFirstDataset
import com.github.HumanLearning2021.HumanLearningApp.hilt.DatabaseManagementModule
import com.github.HumanLearning2021.HumanLearningApp.hilt.Demo2Database
import com.github.HumanLearning2021.HumanLearningApp.model.*
import com.github.HumanLearning2021.HumanLearningApp.view.learning.*
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import java.io.File
import java.util.*


@UninstallModules(DatabaseManagementModule::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class AudioFeedbackTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @BindValue
    @Demo2Database
    val dbManagement: DatabaseManagement = DummyDatabaseManagement(DummyDatabaseService())

    private var datasetPictures = emptySet<CategorizedPicture>()
    private var categories = emptySet<Category>()
    private lateinit var dataset: Dataset
    private val datasetId = getFirstDataset(dbManagement).id as String
    private var index = 0

    private val navController: NavController = Mockito.mock(NavController::class.java)

    @Before
    fun setup() {
        hiltRule.inject()
    }

    fun makeLearningAudioFeedback(): LearningAudioFeedback {
        launchFragment()
        return LearningAudioFeedback(getInstrumentation().targetContext)
    }

    fun assertBothMPsNotPlaying(af: LearningAudioFeedback) {
        launchFragment()
        assertThat(af.__testing_getCorrectMP().isPlaying, `is`(false))
        assertThat(af.__testing_getIncorrectMP().isPlaying, `is`(false))
    }

    @Test
    fun initPutsMediaPlayersInCorrectState() {
        launchFragment()
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
        launchFragment()
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

    private fun launchFragment(){
        val args = bundleOf("datasetId" to datasetId, "learningMode" to LearningMode.PRESENTATION)
        launchFragmentInHiltContainer<LearningFragment>(args) {
            Navigation.setViewNavController(requireView(), navController)
        }
    }
}
