package com.github.HumanLearning2021.HumanLearningApp.view.learning

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.android.architecture.blueprints.todoapp.launchFragmentInHiltContainer
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.TestUtils
import com.github.HumanLearning2021.HumanLearningApp.firestore.FirestoreDatabaseService
import com.github.HumanLearning2021.HumanLearningApp.hilt.DatabaseManagementModule
import com.github.HumanLearning2021.HumanLearningApp.hilt.Demo2Database
import com.github.HumanLearning2021.HumanLearningApp.model.*
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import junit.framework.AssertionFailedError
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import java.io.File
import java.util.*

<<<<<<< HEAD
//With @UninstallModules(DatabaseManagementModule::class) -> missing binding
//With @BindValue -> Double binding
=======
>>>>>>> 1e186249c7a79db3fbb9b5e4579f23ac4cb64b77

@UninstallModules(DatabaseManagementModule::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class LearningSettingsActivityTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @BindValue @Demo2Database
    val dbManagement: DatabaseManagement = DummyDatabaseManagement(DummyDatabaseService())

<<<<<<< HEAD
    private val datasetId = TestUtils.getFirstDataset(dbManagement).id as String
=======
    private val datasetId = TestUtils.getFirstDataset(dbManagement).id
>>>>>>> 1e186249c7a79db3fbb9b5e4579f23ac4cb64b77


    val navController = mock(NavController::class.java)


    @Before
    fun setup() {
        hiltRule.inject()
        launchFragment()
    }

    @Test
    fun pressingPresentationButtonLaunchesLearningActivity(){

        onView(withId(R.id.learningSettings_btChoosePresentation)).perform(click())


        verify(navController).navigate(
            LearningSettingsFragmentDirections.actionLearningSettingsFragmentToLearningFragment(datasetId, LearningMode.PRESENTATION)
        )
    }

    @Test
<<<<<<< HEAD
    fun pressingReresentationButtonLaunchesLearningActivity(){
=======
    fun pressingRepresentationButtonLaunchesLearningActivity(){
>>>>>>> 1e186249c7a79db3fbb9b5e4579f23ac4cb64b77
        onView(withId(R.id.learningSettings_btChooseRepresentation)).perform(click())
        verify(navController).navigate(
            LearningSettingsFragmentDirections.actionLearningSettingsFragmentToLearningFragment(datasetId, LearningMode.REPRESENTATION)
        )
    }

    private fun bothButtonsAndTVAreDisplayed() {
        assertDisplayed(R.id.learningSettings_btChoosePresentation)
        assertDisplayed(R.id.learningSettings_btChooseRepresentation)
        assertDisplayed(R.id.learningSettings_tvMode)
    }

    @Test
    fun learningModeTooltipsAreCorrect() {
        val res = InstrumentationRegistry.getInstrumentation().targetContext.resources
        onView(withId(R.id.learningSettings_btChoosePresentation))
            .check(HasTooltipText(res.getString(R.string.learning_settings_tooltip_presentation)))
        onView(withId(R.id.learningSettings_btChooseRepresentation))
            .check(HasTooltipText(res.getString(R.string.learning_settings_tooltip_representation)))
    }

    @Test
    fun staticUITests(){
        learningModeTooltipsAreCorrect()
        bothButtonsAndTVAreDisplayed()
    }


    inner class HasTooltipText(private val text: String) : ViewAssertion {
        override fun check(view: View?, noViewFoundException: NoMatchingViewException?) {
            if (view != null && noViewFoundException == null) {
                if (view.tooltipText.toString() != text) {
                    throw AssertionFailedError(
                        "The tooltip text was different than expected " +
                                "was \"${view.tooltipText}\", expected \"$text\""
                    )
                }
            } else {
                throw AssertionFailedError("The view was not found")
            }
        }
    }

    private fun launchFragment(){
        val args = bundleOf("datasetId" to datasetId)

        launchFragmentInHiltContainer<LearningSettingsFragment>(fragmentArgs = args) {
            Navigation.setViewNavController(requireView(), navController)
        }

    }
<<<<<<< HEAD
}
=======
}
>>>>>>> 1e186249c7a79db3fbb9b5e4579f23ac4cb64b77
