package com.github.HumanLearning2021.HumanLearningApp.view.learning

import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.android.architecture.blueprints.todoapp.launchFragmentInHiltContainer
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.TestUtils
import com.github.HumanLearning2021.HumanLearningApp.hilt.DatabaseManagementModule
import com.github.HumanLearning2021.HumanLearningApp.hilt.Demo2Database
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.DefaultDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.DummyDatabaseService
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import junit.framework.AssertionFailedError
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@UninstallModules(DatabaseManagementModule::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class LearningSettingsFragmentTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @BindValue
    @Demo2Database
    val dbManagement: DatabaseManagement = DefaultDatabaseManagement(DummyDatabaseService())

    private val datasetId = TestUtils.getFirstDataset(dbManagement).id

    val navController = mock(NavController::class.java)


    @Before
    fun setup() {
        hiltRule.inject()
        launchFragment()
    }

    @Test
    fun pressingPresentationButtonLaunchesLearningActivity() {

        onView(withId(R.id.learningSettings_btChoosePresentation)).perform(click())


        verify(navController).navigate(
            LearningSettingsFragmentDirections.actionLearningSettingsFragmentToLearningFragment(
                datasetId,
                LearningMode.PRESENTATION
            )
        )
    }

    @Test
    fun pressingRepresentationButtonLaunchesLearningActivity() {
        onView(withId(R.id.learningSettings_btChooseRepresentation)).perform(click())
        verify(navController).navigate(
            LearningSettingsFragmentDirections.actionLearningSettingsFragmentToLearningFragment(
                datasetId,
                LearningMode.REPRESENTATION
            )
        )
    }

    @Test
    fun pressingEvaluationButtonLaunchesLearningActivity() {
        onView(withId(R.id.learningSettings_btChooseEvaluation)).perform(click())
        verify(navController).navigate(
            LearningSettingsFragmentDirections.actionLearningSettingsFragmentToLearningFragment(
                datasetId,
                LearningMode.EVALUATION
            )
        )
    }

    private fun titleTestAndButtonsDisplayed() {
        assertDisplayed(R.id.learningSettings_btChoosePresentation)
        assertDisplayed(R.id.learningSettings_btChooseRepresentation)
        assertDisplayed(R.id.learningSettings_btChooseEvaluation)
        assertDisplayed(R.id.learningSettings_tvMode)
    }

    private fun learningModeTooltipsAreCorrect() {
        val res = InstrumentationRegistry.getInstrumentation().targetContext.resources
        onView(withId(R.id.learningSettings_btChoosePresentation))
            .check(HasTooltipText(res.getString(R.string.learning_settings_tooltip_presentation)))
        onView(withId(R.id.learningSettings_btChooseRepresentation))
            .check(HasTooltipText(res.getString(R.string.learning_settings_tooltip_representation)))
        onView(withId(R.id.learningSettings_btChooseEvaluation))
            .check(HasTooltipText(res.getString(R.string.learning_settings_tooltip_evaluation)))
    }

    @Test
    fun staticUITests() {
        learningModeTooltipsAreCorrect()
        titleTestAndButtonsDisplayed()
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

    private fun launchFragment() {
        val args = bundleOf("datasetId" to datasetId)

        launchFragmentInHiltContainer<LearningSettingsFragment>(fragmentArgs = args) {
            Navigation.setViewNavController(requireView(), navController)
        }

    }
}
