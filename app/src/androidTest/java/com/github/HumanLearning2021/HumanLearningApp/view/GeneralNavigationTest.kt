package com.github.HumanLearning2021.HumanLearningApp.view

import android.content.Intent
import androidx.navigation.fragment.findNavController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.DrawerMatchers.isOpen
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.firebase.ui.auth.AuthUI
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.hilt.DatabaseNameModule
import com.github.HumanLearning2021.HumanLearningApp.hilt.ProductionDatabaseName
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseManagement
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
import org.hamcrest.CoreMatchers.equalTo
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@UninstallModules(DatabaseNameModule::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class GeneralNavigationTest {

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

    private lateinit var dbMgt: DatabaseManagement

    @BindValue
    val authPresenter = AuthenticationPresenter(AuthUI.getInstance(), DummyDatabaseService())

    @Before
    fun setup() {
        hiltRule.inject()
        dbMgt = globalDatabaseManagement.accessDatabase(dbName)
        Intents.init()
    }

    @After
    fun cleanUp() {
        Intents.release()
        activityScenarioRule.scenario.close()
    }


    @Test // this one used to cause trouble
    fun navigateToDisplayDatasetAndThenNavigateUpGoesToDatasetsOverview() {
        runBlocking {
            Firebase.auth.signInAnonymously().await().user!!
            authPresenter.onSuccessfulLogin(true)
            onView(withId(R.id.startLearningButton)).perform(click())
            navigateToDisplayDataset()
            assertCurrentFragmentIsCorrect(R.id.displayDatasetFragment)
            onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())
            assertCurrentFragmentIsCorrect(R.id.datasetsOverviewFragment)
        }
    }

    @Test
    fun createDatasetAndNavigateUpGoesToDatasetsOverview() {
        runBlocking {
            Firebase.auth.signInAnonymously().await().user!!
            authPresenter.onSuccessfulLogin(true)
            onView(withId(R.id.startLearningButton)).perform(click())
            onView(withId(R.id.datasetsOverviewFragment)).perform(click())
            onView(withId(R.id.createDatasetButton)).perform(click())
            assertCurrentFragmentIsCorrect(R.id.categoriesEditingFragment)
            onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())
            assertCurrentFragmentIsCorrect(R.id.datasetsOverviewFragment)
        }
    }

    @Test
    fun openingNavigationAndClickingOnLoginWorks() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open())
        onView(withId(R.id.drawer_layout)).check(matches(isOpen()))
        onView(withId(R.id.googleSignInWidget)).perform(click())
        assertCurrentFragmentIsCorrect(R.id.googleSignInWidget)
    }

    @Test
    fun bottomNavigationCanNavigateToLearning() {
        onView(withId(R.id.learningDatasetSelectionFragment)).perform(click())
        assertCurrentFragmentIsCorrect(R.id.learningDatasetSelectionFragment)
    }

    @Test
    fun bottomNavigationCanNavigateToDatasetEditing() {
        onView(withId(R.id.learningDatasetSelectionFragment)).perform(click())
        assertCurrentFragmentIsCorrect(R.id.learningDatasetSelectionFragment)
    }

    @Test
    fun buttonOnHomeToLearningWorks() {
        onView(withId(R.id.startLearningButton)).perform(click())
        assertCurrentFragmentIsCorrect(R.id.learningDatasetSelectionFragment)
    }

    @Test
    fun addPicturePressBackIsCorrect() {
        runBlocking {
            Firebase.auth.signInAnonymously().await().user!!
            authPresenter.onSuccessfulLogin(true)
            onView(withId(R.id.startLearningButton)).perform(click())
            navigateToDisplayDataset()
            assertCurrentFragmentIsCorrect(R.id.displayDatasetFragment)
            openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().targetContext)
            onView(withText(R.string.add_new_picture)).perform(click())
            pressBack()
            assertCurrentFragmentIsCorrect(R.id.displayDatasetFragment)
        }
    }


    private fun navigateToDisplayDataset() {
        onView(withId(R.id.datasetsOverviewFragment)).perform(click())
        onView(withId(R.id.DatasetList_list))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<DatasetListRecyclerViewAdapter.ListItemViewHolder>(
                    0,
                    click()
                )
            )
    }

    private fun assertCurrentFragmentIsCorrect(expected: Int) {
        activityScenarioRule.scenario.onActivity {
            val currentFragmentContainer =
                it.supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container)
            val currentFragment = currentFragmentContainer?.findNavController()?.currentDestination
            assertThat(currentFragment?.id, equalTo(expected))
        }
    }
}
