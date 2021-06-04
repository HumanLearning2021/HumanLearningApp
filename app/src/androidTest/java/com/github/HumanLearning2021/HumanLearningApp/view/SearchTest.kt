package com.github.HumanLearning2021.HumanLearningApp.view

import android.content.Intent
import androidx.navigation.fragment.findNavController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.firebase.ui.auth.AuthUI
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.TestUtils.setTextInSearchView
import com.github.HumanLearning2021.HumanLearningApp.hilt.DatabaseNameModule
import com.github.HumanLearning2021.HumanLearningApp.hilt.ProductionDatabaseName
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.Dataset
import com.github.HumanLearning2021.HumanLearningApp.model.DummyDatabaseService
import com.github.HumanLearning2021.HumanLearningApp.model.UniqueDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.presenter.AuthenticationPresenter
import com.github.HumanLearning2021.HumanLearningApp.view.dataset_list_fragment.DatasetListRecyclerViewAdapter
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.hamcrest.CoreMatchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@UninstallModules(DatabaseNameModule::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class SearchTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val activityScenarioRule: ActivityScenarioRule<MainActivity> = ActivityScenarioRule(
        Intent(
            ApplicationProvider.getApplicationContext(),
            MainActivity::class.java
        )
    )

    @Inject
    lateinit var globalDatabaseManagement: UniqueDatabaseManagement

    @BindValue
    @ProductionDatabaseName
    val dbName = "dummy"

    lateinit var dbMgt: DatabaseManagement

    @BindValue
    val authPresenter = AuthenticationPresenter(AuthUI.getInstance(), DummyDatabaseService())

    private lateinit var dummyDatasets: Set<Dataset>

    private val datasetName = "kitchen utensils"

    /**
     * The discriminating prefix is used as a search text in tests below. When searching
     * with this prefix, only the dataset with name `datasetName` should be visible.
     */
    private val discriminatingPrefix = "kitchen"

    @Before
    fun setup() {
        hiltRule.inject()
        dbMgt = globalDatabaseManagement.accessDatabase(dbName)
        dummyDatasets = runBlocking { dbMgt.getDatasets() }
        runBlocking {
            require(dbMgt.getDatasetByName(datasetName).size == 1) {
                "The database has to contain exactly one dataset with name $datasetName " +
                        "to be able to proceed."
            }
        }
        Intents.init()
    }

    @After
    fun cleanUp() {
        Intents.release()
        activityScenarioRule.scenario.close()
    }

    @Test
    fun datasetsOverviewSearchByKeyWordYieldsCorrectResult() {
        runBlocking {
            Firebase.auth.signInAnonymously().await().user!!
            authPresenter.onSuccessfulLogin(true)
            onView(withId(R.id.button_start_learning)).perform(click())
            navigateToDatasetsOverview()
            searchByKeyWordYieldsCorrectResult()
        }
    }


    @Test
    fun learningDatasetSelectionSearchByKeyWordYieldsCorrectResult() {
        navigateToLearningDatasetSelection()
        searchByKeyWordYieldsCorrectResult()
    }

    @Test
    fun datasetsOverviewSearchNotFoundYieldsNoResult() {
        runBlocking {
            Firebase.auth.signInAnonymously().await().user!!
            authPresenter.onSuccessfulLogin(true)
            onView(withId(R.id.button_start_learning)).perform(click())
            navigateToDatasetsOverview()
            searchNotFoundYieldsNoResult()
        }
    }

    @Test
    fun learningDatasetSelectionSearchNotFoundYieldsNoResult() {
        navigateToLearningDatasetSelection()
        searchNotFoundYieldsNoResult()
    }


    @Test
    fun datasetsOverviewCanClickOnSubsetOfDatasetsMatchingSearch() {
        runBlocking {
            Firebase.auth.signInAnonymously().await().user!!
            authPresenter.onSuccessfulLogin(true)
            onView(withId(R.id.button_start_learning)).perform(click())
            navigateToDatasetsOverview()
            canClickOnSubsetOfDatasetsMatchingSearch()
            assertCurrentFragmentIsCorrect(R.id.displayDatasetFragment)
        }
    }

    @Test
    fun learningDatasetSelectionCanClickOnSubsetOfDatasetsMatchingSearch() {
        navigateToLearningDatasetSelection()
        canClickOnSubsetOfDatasetsMatchingSearch()
        assertCurrentFragmentIsCorrect(R.id.learningSettingsFragment)
    }

    @Test
    fun datasetsOverviewEmptySpacePrefixHasNoInfluence() {
        runBlocking {
            Firebase.auth.signInAnonymously().await().user!!
            authPresenter.onSuccessfulLogin(true)
            onView(withId(R.id.button_start_learning)).perform(click())
            navigateToDatasetsOverview()
            emptySpacePrefixHasNoInfluence()
        }
    }

    @Test
    fun learningDatasetSelectionEmptySpacePrefixHasNoInfluence() {
        navigateToLearningDatasetSelection()
        emptySpacePrefixHasNoInfluence()
    }

    @Test
    fun datasetsOverviewTypeTextAndThenClearYieldsAllResults() {
        runBlocking {
            Firebase.auth.signInAnonymously().await().user!!
            authPresenter.onSuccessfulLogin(true)
        }
        onView(withId(R.id.button_start_learning)).perform(click())
        navigateToDatasetsOverview()
        typeTextAndThenClearYieldsAllResults()
    }

    @Test
    fun learningDatasetSelectionTypeTextAndThenClearYieldsAllResults() {
        navigateToLearningDatasetSelection()
        typeTextAndThenClearYieldsAllResults()
    }

    private fun navigateToDatasetsOverview() {
        onView(withId(R.id.datasetsOverviewFragment)).perform(click())
    }

    private fun navigateToLearningDatasetSelection() {
        onView(withId(R.id.learningDatasetSelectionFragment)).perform(click())
    }


    private fun emptySpacePrefixHasNoInfluence() {
        onView(withId(R.id.action_search)).perform(click(), typeText("          "))
        onView(isRoot()).perform(closeSoftKeyboard())
        onView(withId(R.id.recyclerView_dataset_list)).check(
            ViewAssertions.matches(
                ViewMatchers.hasChildCount(dummyDatasets.size)
            )
        )
    }

    private fun typeTextAndThenClearYieldsAllResults() {
        onView(withId(R.id.action_search)).perform(
            click(),
            typeText(discriminatingPrefix),
            setTextInSearchView(""),
        )
        onView(withId(R.id.recyclerView_dataset_list)).check(
            ViewAssertions.matches(
                ViewMatchers.hasChildCount(dummyDatasets.size)
            )
        )
    }

    private fun canClickOnSubsetOfDatasetsMatchingSearch() {
        onView(withId(R.id.action_search)).perform(click(), typeText(discriminatingPrefix))
        onView(withId(R.id.recyclerView_dataset_list))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<DatasetListRecyclerViewAdapter.ListItemViewHolder>(
                    0,
                    click()
                )
            )
    }

    private fun searchNotFoundYieldsNoResult() {
        onView(withId(R.id.action_search)).perform(
            click(),
            typeText("asdfghjkledfvboijhedfvbgbzuhikmolwsxd")
        )
        onView(withId(R.id.recyclerView_dataset_list)).check(
            ViewAssertions.matches(
                ViewMatchers.hasChildCount(0)
            )
        )
    }

    private fun searchByKeyWordYieldsCorrectResult() {
        onView(withId(R.id.action_search)).perform(click(), typeText(discriminatingPrefix))
        onView(withId(R.id.recyclerView_dataset_list)).check(
            ViewAssertions.matches(
                ViewMatchers.hasChildCount(1)
            )
        )
    }


    private fun assertCurrentFragmentIsCorrect(expected: Int) {
        activityScenarioRule.scenario.onActivity {
            val currentFragmentContainer =
                it.supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container)
            val currentFragment = currentFragmentContainer?.findNavController()?.currentDestination
            ViewMatchers.assertThat(currentFragment?.id, CoreMatchers.equalTo(expected))
        }
    }
}
