package com.github.HumanLearning2021.HumanLearningApp.view.learning

import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.example.android.architecture.blueprints.todoapp.launchFragmentInHiltContainer
import com.github.HumanLearning2021.HumanLearningApp.TestUtils.getFirstDataset
import com.github.HumanLearning2021.HumanLearningApp.hilt.DatabaseNameModule
import com.github.HumanLearning2021.HumanLearningApp.hilt.GlobalDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.hilt.ProductionDatabaseName
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.Id
import com.github.HumanLearning2021.HumanLearningApp.model.UniqueDatabaseManagement
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import javax.inject.Inject

@UninstallModules(DatabaseNameModule::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class AudioFeedbackTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    @GlobalDatabaseManagement
    lateinit var globalDatabaseManagement: UniqueDatabaseManagement

    @BindValue
    @ProductionDatabaseName
    val dbName = "dummy"

    lateinit var dbMgt: DatabaseManagement

    private lateinit var datasetId: Id

    private val navController: NavController = Mockito.mock(NavController::class.java)

    @Before
    fun setup() {
        hiltRule.inject()
        dbMgt = globalDatabaseManagement.accessDatabase(dbName)
        datasetId = getFirstDataset(dbMgt).id
        launchFragment()
    }

    private fun makeLearningAudioFeedback(): LearningAudioFeedback {
        return LearningAudioFeedback(getInstrumentation().targetContext)
    }

    private fun assertBothMPsNotPlaying(af: LearningAudioFeedback) {
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

    private fun launchFragment() {
        val args = bundleOf("datasetId" to datasetId, "learningMode" to LearningMode.PRESENTATION)
        launchFragmentInHiltContainer<LearningFragment>(args) {
            Navigation.setViewNavController(requireView(), navController)
        }
    }
}
