package com.github.HumanLearning2021.HumanLearningApp.view.learning

import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.android.architecture.blueprints.todoapp.launchFragmentInHiltContainer
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.hilt.DatabaseManagementModule
import com.github.HumanLearning2021.HumanLearningApp.hilt.ProdDatabase
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.DefaultDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.DummyDatabaseService
import com.github.HumanLearning2021.HumanLearningApp.model.learning.EvaluationResult
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.verify

@UninstallModules(DatabaseManagementModule::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class EvaluationResultFragmentTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @BindValue
    @ProdDatabase
    val dbManagement: DatabaseManagement = DefaultDatabaseManagement(DummyDatabaseService())

    private val navController: NavController = Mockito.mock(NavController::class.java)

    @Before
    fun setup() {
        hiltRule.inject()
        val evaluationResultList = IntRange(
            start = 0,
            endInclusive = 5
        ).map { 2 to 3 } as MutableList<Pair<Int, Int>>

        launchFragment(EvaluationResult(evaluationResultList))
    }

    @Test
    fun graphIsDisplayed() {
        assertDisplayed(R.id.barchart_evaluation_result)
    }


    @Test
    @Ignore("I don't know the library well enough to test its content yet")
    fun rightStuffIsDisplayedOnGraph() {
    }

    @Test
    fun pressingButtonNavigatesToLearningDatasetSelection() {
        onView(withId(R.id.learnAgainButton)).perform(click())
        verify(navController).navigate(EvaluationResultFragmentDirections.actionEvaluationResultFragmentToLearningDatasetSelectionFragment())
    }

    private fun launchFragment(evaluationResult: EvaluationResult) {
        val args = bundleOf("evaluationResult" to evaluationResult)
        launchFragmentInHiltContainer<EvaluationResultFragment>(args) {
            Navigation.setViewNavController(requireView(), navController)
        }
    }
}